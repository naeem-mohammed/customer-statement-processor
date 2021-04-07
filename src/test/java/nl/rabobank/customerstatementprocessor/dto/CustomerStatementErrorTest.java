package nl.rabobank.customerstatementprocessor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerStatementErrorTest {

  @Test
  @DisplayName("Build Customer Statement Error Record with reference and account")
  public void given_StatementObject_When_MapToCustomerStatementError_Then_AssertStatementError() {
    CustomerStatement customerStatement =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    CustomerStatementError customerStatementError =
        CustomerStatementError.mapToError(customerStatement);

    assertEquals(customerStatement.getAccount(), customerStatementError.getAccountNumber());
    assertEquals(customerStatement.getReference(), customerStatementError.getReference());
  }

  @Test
  @DisplayName("Throw Error when Customer Statement Record is null")
  public void given_StatementObjectNull_When_MapToCustomerStatementError_Then_AssertException() {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> CustomerStatementError.mapToError(null));
  }
}
