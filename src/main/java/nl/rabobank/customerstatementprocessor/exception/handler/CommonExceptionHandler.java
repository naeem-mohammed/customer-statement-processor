package nl.rabobank.customerstatementprocessor.config;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.customerstatementprocessor.dto.CommonResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public CommonResponse<Object> handleClientException(Exception ex) {
    log.error("Unexpected Error", ex);
    return new CommonResponse<>(INTERNAL_SERVER_ERROR.name());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(BAD_REQUEST)
  public CommonResponse<Object> handleJsonFormatException(Exception ex) {
    log.error("Json Format Exception", ex);
    return new CommonResponse<>(BAD_REQUEST.name());
  }

  @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
  @ResponseStatus(BAD_REQUEST)
  public CommonResponse<Object> handleConstraintViolationException(Exception ex) {
    log.error("Constraints Violation Exception", ex);
    return new CommonResponse<>(BAD_REQUEST.name());
  }
}
