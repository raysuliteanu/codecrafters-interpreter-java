
package lox;

public class UnexpectedTokenException extends ParseException {
  String actual;

  public UnexpectedTokenException(String actual, Span span) {
    super("Unexpected token", span);
    this.actual = actual;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + actual;
  }
}
