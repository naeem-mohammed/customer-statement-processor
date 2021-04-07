package nl.rabobank.customerstatementprocessor.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerStatementTest {

  @Test
  @DisplayName("Check if two different Customer Statement Records are not equal")
  public void
      given_TwoCustomerStatementsWithDifferentReferences_When_CheckingEquals_Then_AssertNotEqual() {

    CustomerStatement customerStatementOne =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    CustomerStatement customerStatementTwo =
        CustomerStatement.builder().reference(2L).account("NL").description("anything").build();

    assertNotEquals(customerStatementOne, customerStatementTwo);
  }

  @Test
  @DisplayName("Check if two same Customer Statement Records are equal")
  public void given_SameCustomerStatement_When_CheckingEquals_Then_AssertEqual() {

    CustomerStatement customerStatementOne =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    CustomerStatement customerStatementTwo =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    assertEquals(customerStatementOne, customerStatementTwo);
  }

  @Test
  @DisplayName("Check if valid Customer Statement Record and a null record is not equal")
  public void given_TwoCustomerStatementsOneNull_When_CheckingEquals_Then_AssertNotEqual() {

    CustomerStatement customerStatement =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    assertNotEquals(customerStatement, null);
  }

  @Test
  @DisplayName("Check if two valid Customer Statement Record with same reference is equal")
  public void
      given_TwoCustomerStatementsWithEqualReferences_When_CheckingEquals_Then_AssertEqual() {

    CustomerStatement customerStatementOne =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    CustomerStatement customerStatementTwo =
        CustomerStatement.builder().reference(1L).account("NL").description("anything").build();

    assertEquals(customerStatementOne, customerStatementTwo);
  }
}
