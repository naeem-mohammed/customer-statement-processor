package nl.rabobank.customerstatementprocessor.controller.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "Validation")
@RequestMapping("/validate")
public interface ValidationApi {

  @ApiOperation(
      produces = APPLICATION_JSON_VALUE,
      consumes = APPLICATION_JSON_VALUE,
      response = CommonResponse.class,
      notes =
          "Validate monthly deliveries of customer statement(JSON) records and returns the "
              + "result of the validation",
      value = "Validate monthly deliveries of customer statement(JSON) records")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Successfully validated the customer statement records"),
    @ApiResponse(code = 400, message = "Error during parsing JSON"),
    @ApiResponse(code = 500, message = "Any other situation")
  })
  @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<?> validate(
      @RequestBody @Valid List<CustomerStatement> customerCustomerStatements);
}
