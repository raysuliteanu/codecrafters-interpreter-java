package lox;

public class UnexpectedTokenException extends ParseException {
  Character actual;

  public UnexpectedTokenException(Character actual) {
    this(actual, null);
  }

  public UnexpectedTokenException(Character actual, Span span) {
    super("Unexpected token", span);
    this.actual = actual;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + actual;
  }
}
