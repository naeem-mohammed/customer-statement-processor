package nl.rabobank.customerstatementprocessor.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.math.BigDecimal;
import java.util.List;
import nl.rabobank.customerstatementprocessor.ObjectMapperProvider;
import nl.rabobank.customerstatementprocessor.StatementHelper;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatementError;
import nl.rabobank.customerstatementprocessor.dto.ValidationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerStatementValidationControllerIntegratedTest {

  private final ObjectReader responseErrorReader =
      ObjectMapperProvider.get()
          .readerFor(new TypeReference<CommonResponse<CustomerStatementError>>() {});
  private final ObjectWriter statementListWriter =
      ObjectMapperProvider.get().writerFor(new TypeReference<List<CustomerStatement>>() {});

  @Autowired private MockMvc mockMvc;

  @Test
  public void Given_ValidCustomerStatements_When_Validate_Then_AssertSuccess() throws Exception {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);

    String payload = statementListWriter.writeValueAsString(customerStatements);
    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    CommonResponse<CustomerStatementError> commonResponse =
        responseErrorReader.readValue(response.getContentAsString());
    assertEquals(ValidationStatus.SUCCESSFUL.getValue(), commonResponse.getResult());
    assertTrue(commonResponse.getErrorRecords().isEmpty());
  }

  @Test
  public void Given_DuplicateCustomerStatements_When_Validate_Then_AssertDuplicateReference()
      throws Exception {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);
    CustomerStatement duplicateCustomerStatement = StatementHelper.validCustomerStatement(1);
    CustomerStatement secondDuplicateCustomerStatement = StatementHelper.validCustomerStatement(4);
    customerStatements.add(duplicateCustomerStatement);
    customerStatements.add(secondDuplicateCustomerStatement);

    String payload = statementListWriter.writeValueAsString(customerStatements);
    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    CommonResponse<CustomerStatementError> commonResponse =
        responseErrorReader.readValue(response.getContentAsString());
    assertEquals(ValidationStatus.DUPLICATE_REFERENCE.getValue(), commonResponse.getResult());
    assertEquals(2, commonResponse.getErrorRecords().size());
    assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(1L))));
    assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(4L))));
  }

  @Test
  public void Given_IncorrectEndBalanceStatements_When_Validate_Then_AssertDuplicateReference()
      throws Exception {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);
    CustomerStatement incorrectEndBalanceCustomerStatement =
        CustomerStatement.builder()
            .reference(11L)
            .account("NL")
            .description("any")
            .startBalance(BigDecimal.valueOf(10.01D))
            .mutation(BigDecimal.valueOf(-10.01D))
            .endBalance(BigDecimal.valueOf(10.01D))
            .build();
    CustomerStatement secondIncorrectEndBalanceCustomerStatement =
        CustomerStatement.builder()
            .reference(12L)
            .account("NL")
            .description("any")
            .startBalance(BigDecimal.valueOf(1D))
            .mutation(BigDecimal.valueOf(1D))
            .endBalance(BigDecimal.valueOf(1D))
            .build();
    customerStatements.add(incorrectEndBalanceCustomerStatement);
    customerStatements.add(secondIncorrectEndBalanceCustomerStatement);

    String payload = statementListWriter.writeValueAsString(customerStatements);
    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    CommonResponse<CustomerStatementError> commonResponse =
        responseErrorReader.readValue(response.getContentAsString());
    assertEquals(ValidationStatus.INCORRECT_END_BALANCE.getValue(), commonResponse.getResult());
    assertEquals(2, commonResponse.getErrorRecords().size());
    assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(11L))));
    assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(12L))));
  }

  @Test
  public void
      Given_IncorrectEndBalanceAndDuplicateStatements_When_Validate_Then_AssertDuplicateReference()
          throws Exception {
    List<CustomerStatement> customerStatements = StatementHelper.getValidCustomerStatements(5);
    CustomerStatement incorrectEndBalanceCustomerStatement =
        CustomerStatement.builder()
            .reference(11L)
            .account("NL")
            .description("any")
            .startBalance(BigDecimal.valueOf(10.01D))
            .mutation(BigDecimal.valueOf(-10.01D))
            .endBalance(BigDecimal.valueOf(10.01D))
            .build();
    CustomerStatement duplicateCustomerStatement = StatementHelper.validCustomerStatement(1);
    customerStatements.add(incorrectEndBalanceCustomerStatement);
    customerStatements.add(duplicateCustomerStatement);

    String payload = statementListWriter.writeValueAsString(customerStatements);
    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    CommonResponse<CustomerStatementError> commonResponse =
        responseErrorReader.readValue(response.getContentAsString());
    assertEquals(
        ValidationStatus.DUPLICATE_AND_INCORRECT_END_BALANCE.getValue(),
        commonResponse.getResult());
    assertEquals(2, commonResponse.getErrorRecords().size());
    assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(1L))));
    assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(11L))));
  }
}
