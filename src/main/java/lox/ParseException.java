
package lox;

public class ParseException extends LoxException {
  private Span span;

  public ParseException(String message) {
    super(message);
  }

  public ParseException(Span span) {
    super();
    this.span = span;
  }

  public ParseException(String message, Span span) {
    super(message);
    this.span = span;
  }

  public Span getSpan() {
    return span;
  }

  @Override
  public String toString() {
    return "[line " + span.line() + "]: " + super.toString();
  }
}
