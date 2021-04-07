package nl.rabobank.customerstatementprocessor.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nl.rabobank.customerstatementprocessor.controller.api.ValidationApi;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import nl.rabobank.customerstatementprocessor.service.CustomerStatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Validated
public class ValidationController implements ValidationApi {

  private final CustomerStatementService customerStatementService;

  @Override
  public ResponseEntity<?> validate(List<CustomerStatement> customerStatements) {
    return ok(customerStatementService.validate(customerStatements));
  }
}
