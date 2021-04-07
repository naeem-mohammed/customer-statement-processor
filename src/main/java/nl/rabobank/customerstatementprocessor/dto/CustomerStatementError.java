package nl.rabobank.customerstatementprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerStatementError {

  private Long reference;

  private String accountNumber;

  public static CustomerStatementError mapToError(CustomerStatement customerStatement) {
    if (customerStatement == null) {
      throw new IllegalArgumentException(CustomerStatement.class.getSimpleName());
    }
    return new CustomerStatementError(
        customerStatement.getReference(), customerStatement.getAccount());
  }
}
