package nl.rabobank.customerstatementprocessor.service.exception;

public class InvalidStatementJsonParseException extends Exception {

  private static final long serialVersionUID = -8537761207054290697L;

  public InvalidStatementJsonParseException(String message) {
    super(message);
  }
}
