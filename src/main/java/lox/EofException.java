
package lox;

public class EofException extends ParseException {
  public EofException() {
    super("Unexpected EOF");
  }

  public EofException(Span span) {
    super(span);
  }

  public EofException(String message, Span span) {
    super(message, span);
  }
}
