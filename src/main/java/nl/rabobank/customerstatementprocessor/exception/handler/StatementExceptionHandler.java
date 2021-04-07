package nl.rabobank.customerstatementprocessor.controller.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import nl.rabobank.customerstatementprocessor.dto.CustomerStatementError;
import nl.rabobank.customerstatementprocessor.service.exception.InvalidStatementException;
import nl.rabobank.customerstatementprocessor.service.exception.InvalidStatementJsonParseException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StatementExceptionHandler {

  @ExceptionHandler(InvalidStatementJsonParseException.class)
  @ResponseStatus(BAD_REQUEST)
  public CommonResponse<Object> handleInvalidStatementJsonParseException(
      InvalidStatementJsonParseException ex) {
    log.error("Invalid Customer Statement JSON", ex);
    return new CommonResponse<>(BAD_REQUEST.name());
  }

  @ExceptionHandler(InvalidStatementException.class)
  @ResponseStatus(OK)
  public CommonResponse<CustomerStatementError> handleInvalidStatementJsonParseException(
      InvalidStatementException ex) {
    log.error("Invalid Customer Statement Record", ex);
    List<CustomerStatementError> customerStatementErrors =
        ex.getRecords().stream()
            .map(CustomerStatementError::mapToError)
            .collect(Collectors.toList());
    return new CommonResponse<>(ex.getStatus().getValue(), customerStatementErrors);
  }
}
