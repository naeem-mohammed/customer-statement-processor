package nl.rabobank.customerstatementprocessor.service;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import nl.rabobank.customerstatementprocessor.StatementHelper;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import nl.rabobank.customerstatementprocessor.dto.ValidationStatus;
import nl.rabobank.customerstatementprocessor.service.exception.InvalidStatementException;
import nl.rabobank.customerstatementprocessor.service.exception.InvalidStatementJsonParseException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerStatementServiceTest {

  private final CustomerStatementService customerStatementService = new CustomerStatementService();

  @Test
  @DisplayName("Throw error when given Customer Statement Record is null")
  public void Given_NullCustomerStatementList_When_Validate_Then_AssertThrowException() {
    Assertions.assertThrows(
        InvalidStatementJsonParseException.class,
        () -> customerStatementService.validate(null),
        "Empty Customer Statement");
  }

  @Test
  @DisplayName("Throw error when given Customer Statement Record is an empty list")
  public void Given_EmptyCustomerStatementList_When_Validate_Then_AssertThrowException() {
    List<CustomerStatement> customerStatements = emptyList();
    Assertions.assertThrows(
        InvalidStatementJsonParseException.class,
        () -> customerStatementService.validate(customerStatements),
        "Empty Customer Statement");
  }

  @Test
  @DisplayName(
      "When 2 Customer Statement Records have same reference, give result with status as "
          + "DUPLICATE_REFERENCE and record list containing it's reference number")
  public void
      Given_DuplicatedCustomerStatement_When_Validate_Then_AssertThrowInvalidStatementException() {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);
    CustomerStatement duplicateCustomerStatement = StatementHelper.validCustomerStatement(1);
    customerStatements.add(duplicateCustomerStatement);

    InvalidStatementException exception =
        assertThrows(
            InvalidStatementException.class,
            () -> customerStatementService.validate(customerStatements));
    Assertions.assertEquals(ValidationStatus.DUPLICATE_REFERENCE, exception.getStatus());
    assertEquals(1, exception.getRecords().size());
    MatcherAssert.assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(1L))));
  }

  @Test
  @DisplayName(
      "When Customer Statement Record has incorrect end balance, give result with "
          + "status as INCORRECT_END_BALANCE and record list containing it's reference number")
  public void
      Given_IncorrectEndBalanceCustomerStatement_When_Validate_Then_AssertThrowInvalidStatementException() {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);
    CustomerStatement incorrectEndBalanceCustomerStatement =
        CustomerStatement.builder()
            .reference(10L)
            .account("NL BALANCE")
            .startBalance(new BigDecimal(10))
            .mutation(new BigDecimal(-10))
            .endBalance(new BigDecimal(20))
            .build();
    customerStatements.add(incorrectEndBalanceCustomerStatement);

    InvalidStatementException exception =
        assertThrows(
            InvalidStatementException.class,
            () -> customerStatementService.validate(customerStatements));
    Assertions.assertEquals(ValidationStatus.INCORRECT_END_BALANCE, exception.getStatus());
    assertEquals(1, exception.getRecords().size());
    MatcherAssert.assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(10L))));
  }

  @Test
  @DisplayName(
      "When Customer Statement Records have incorrect end balance and duplicate "
          + "references, give result with status as DUPLICATE_AND_INCORRECT_END_BALANCE and record "
          + "list containing it's reference number")
  public void
      Given_IncorrectEndBalanceAndDuplicatedCustomerStatement_When_Validate_Then_AssertThrowInvalidStatementException() {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);
    CustomerStatement incorrectEndBalanceCustomerStatement =
        CustomerStatement.builder()
            .reference(10L)
            .account("NL BALANCE")
            .startBalance(new BigDecimal(10))
            .mutation(new BigDecimal(-10))
            .endBalance(new BigDecimal(20))
            .build();
    customerStatements.add(incorrectEndBalanceCustomerStatement);
    CustomerStatement duplicateCustomerStatement = StatementHelper.validCustomerStatement(1);
    customerStatements.add(duplicateCustomerStatement);

    InvalidStatementException exception =
        assertThrows(
            InvalidStatementException.class,
            () -> customerStatementService.validate(customerStatements));
    Assertions.assertEquals(
        ValidationStatus.DUPLICATE_AND_INCORRECT_END_BALANCE, exception.getStatus());
    assertEquals(2, exception.getRecords().size());
    MatcherAssert.assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(1L))));
    MatcherAssert.assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(10L))));
  }

  @Test
  @DisplayName(
      "When Customer Statement Records are valid, return status as SUCCESSFUL and empty "
          + "record list")
  public void Given_ValidCustomerStatements_When_Validate_Then_AssertSuccess() {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);

    CommonResponse<?> response = customerStatementService.validate(customerStatements);

    assertEquals(ValidationStatus.SUCCESSFUL.getValue(), response.getResult());
    assertTrue(response.getErrorRecords().isEmpty());
  }
}
