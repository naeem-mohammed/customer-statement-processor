package nl.rabobank.customerstatementprocessor.service.exception;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatement;
import nl.rabobank.customerstatementprocessor.dto.ValidationStatus;

@RequiredArgsConstructor
@Getter
public class InvalidStatementException extends Exception {

  private static final long serialVersionUID = -2800000311145027431L;

  private final ValidationStatus status;

  private final List<CustomerStatement> records;
}
