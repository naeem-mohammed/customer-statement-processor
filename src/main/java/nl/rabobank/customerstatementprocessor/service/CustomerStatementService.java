package nl.rabobank.customerstatementprocessor.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.SneakyThrows;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import nl.rabobank.customerstatementprocessor.dto.ValidationStatus;
import nl.rabobank.customerstatementprocessor.service.exception.InvalidStatementException;
import nl.rabobank.customerstatementprocessor.service.exception.InvalidStatementJsonParseException;
import org.springframework.stereotype.Service;

@Service
public class CustomerStatementService {

  @SneakyThrows
  public CommonResponse<?> validate(@Valid List<CustomerStatement> customerStatements) {

    validateStatementList(customerStatements);

    List<CustomerStatement> incorrectEndBalanceCustomerStatements = new ArrayList<>();
    Set<CustomerStatement> uniqueCustomerStatements = new HashSet<>();
    Set<CustomerStatement> duplicateCustomerStatements =
        customerStatements.stream()
            .peek(validateEndBalance().apply(incorrectEndBalanceCustomerStatements))
            .filter(duplicate(uniqueCustomerStatements))
            .collect(Collectors.toSet());

    checkValidation(duplicateCustomerStatements, incorrectEndBalanceCustomerStatements);
    return new CommonResponse<>(ValidationStatus.SUCCESSFUL.getValue());
  }

  public Function<List<CustomerStatement>, Consumer<CustomerStatement>> validateEndBalance() {
    return incorrectEndBalanceCustomerStatements ->
        customerStatement -> {
          if (isEndBalanceIncorrect(customerStatement)) {
            incorrectEndBalanceCustomerStatements.add(customerStatement);
          }
        };
  }

  private boolean isEndBalanceIncorrect(CustomerStatement customerStatement) {
    return !customerStatement
        .getStartBalance()
        .add(customerStatement.getMutation())
        .equals(customerStatement.getEndBalance());
  }

  public Predicate<CustomerStatement> duplicate(Set<CustomerStatement> uniqueCustomerStatements) {
    return customerStatement -> !uniqueCustomerStatements.add(customerStatement);
  }

  private void checkValidation(
      Set<CustomerStatement> duplicateCustomerStatements,
      List<CustomerStatement> incorrectEndBalanceCustomerStatements)
      throws InvalidStatementException {
    if (!duplicateCustomerStatements.isEmpty()
        && !incorrectEndBalanceCustomerStatements.isEmpty()) {
      incorrectEndBalanceCustomerStatements.addAll(0, duplicateCustomerStatements);
      throw new InvalidStatementException(
          ValidationStatus.DUPLICATE_AND_INCORRECT_END_BALANCE,
          incorrectEndBalanceCustomerStatements);
    }
    if (!duplicateCustomerStatements.isEmpty()) {
      throw new InvalidStatementException(
          ValidationStatus.DUPLICATE_REFERENCE, new ArrayList<>(duplicateCustomerStatements));
    }
    if (!incorrectEndBalanceCustomerStatements.isEmpty()) {
      throw new InvalidStatementException(
          ValidationStatus.INCORRECT_END_BALANCE, incorrectEndBalanceCustomerStatements);
    }
  }

  private void validateStatementList(@Valid List<CustomerStatement> customerStatements)
      throws InvalidStatementJsonParseException {
    if (customerStatements == null || customerStatements.isEmpty()) {
      throw new InvalidStatementJsonParseException("Empty Customer Statement");
    }
  }
}
