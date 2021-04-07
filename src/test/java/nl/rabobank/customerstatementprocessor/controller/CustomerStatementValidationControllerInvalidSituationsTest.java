package nl.rabobank.customerstatementprocessor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import nl.rabobank.customerstatementprocessor.ObjectMapperProvider;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerStatementValidationControllerInvalidSituationsTest {

  private final ObjectReader responseErrorReader =
      ObjectMapperProvider.get().readerFor(CommonResponse.class);
  private final ObjectWriter statementWriter =
      ObjectMapperProvider.get().writerFor(CustomerStatement.class);
  private final ObjectWriter statementListWriter =
      ObjectMapperProvider.get().writerFor(new TypeReference<List<CustomerStatement>>() {});

  @Autowired private MockMvc mockMvc;

  @Test
  public void given_InvalidMediaType_When_PostValidation_Then_AssertInternalServerErrorStatus()
      throws Exception {
    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content("any content").contentType(MediaType.TEXT_PLAIN))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse();

    CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());
    assertEquals(INTERNAL_SERVER_ERROR.name(), commonResponse.getResult());
  }

  @Test
  public void given_SimpleObject_When_PostValidation_Then_AssertBadRequestStatus()
      throws Exception {

    CustomerStatement customerStatement =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("anything")
            .startBalance(BigDecimal.valueOf(20.00D))
            .mutation(BigDecimal.valueOf(20.00D))
            .endBalance(BigDecimal.valueOf(20.00D))
            .build();

    String payload = statementWriter.writeValueAsString(customerStatement);

    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse();

    CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());
    assertEquals(BAD_REQUEST.name(), commonResponse.getResult());
  }

  @Test
  public void given_EmptyArray_When_PostValidation_Then_AssertBadRequestStatus() throws Exception {

    String payload = "[]";

    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse();

    CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());
    assertEquals(BAD_REQUEST.name(), commonResponse.getResult());
  }

  @Test
  public void given_AllFieldsNull_When_PostValidation_Then_AssertBadRequestStatus()
      throws Exception {

    String payload = "[{}]";

    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse();

    CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());
    assertEquals(BAD_REQUEST.name(), commonResponse.getResult());
  }

  @Test
  public void
      given_ListOfStatementsMissingOnePropertyEach_When_PostValidation_Then_AssertBadRequestStatus()
          throws Exception {

    BigDecimal value = BigDecimal.valueOf(20.00D);

    CustomerStatement customerStatementNoReference =
        CustomerStatement.builder()
            .account("NL")
            .description("any")
            .startBalance(value)
            .mutation(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementNoAccount =
        CustomerStatement.builder()
            .reference(1L)
            .description("any")
            .startBalance(value)
            .mutation(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementNoDescription =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .startBalance(value)
            .mutation(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementNoStartBalance =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("any")
            .mutation(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementNoMutation =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("any")
            .startBalance(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementNoEndBalance =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("any")
            .startBalance(value)
            .mutation(value)
            .build();

    List<CustomerStatement> customerStatements =
        Arrays.asList(
            customerStatementNoReference,
            customerStatementNoAccount,
            customerStatementNoDescription,
            customerStatementNoStartBalance,
            customerStatementNoMutation,
            customerStatementNoEndBalance);

    String payload = statementListWriter.writeValueAsString(customerStatements);

    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse();

    CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());
    assertEquals(BAD_REQUEST.name(), commonResponse.getResult());
  }

  @Test
  public void given_ListOfInvalidDecimals_When_PostValidation_Then_AssertBadRequestStatus()
      throws Exception {

    BigDecimal value = BigDecimal.valueOf(20.00D);

    CustomerStatement validCustomerStatement =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .startBalance(value)
            .description("any")
            .mutation(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementInvalidStartBalance =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("any")
            .startBalance(BigDecimal.valueOf(20.123D))
            .mutation(value)
            .endBalance(value)
            .build();

    CustomerStatement customerStatementInvalidMutation =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("any")
            .startBalance(value)
            .mutation(BigDecimal.valueOf(20.123D))
            .endBalance(value)
            .build();

    CustomerStatement customerStatementInvalidEndBalance =
        CustomerStatement.builder()
            .reference(1L)
            .account("NL")
            .description("any")
            .startBalance(value)
            .endBalance(BigDecimal.valueOf(20.123D))
            .mutation(value)
            .build();

    List<CustomerStatement> customerStatements =
        Arrays.asList(
            validCustomerStatement,
            customerStatementInvalidStartBalance,
            customerStatementInvalidMutation,
            customerStatementInvalidEndBalance);

    String payload = statementListWriter.writeValueAsString(customerStatements);

    MockHttpServletResponse response =
        mockMvc
            .perform(post("/validate").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse();

    CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());
    assertEquals(BAD_REQUEST.name(), commonResponse.getResult());
  }
}
