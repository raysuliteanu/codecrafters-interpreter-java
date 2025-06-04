package lox;

public class UnexpectedCharacterException extends ParseException {
  final Character actual;

  public UnexpectedCharacterException(Character actual, Span span) {
    super("Unexpected character", span);
    this.actual = actual;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + actual;
  }
}
