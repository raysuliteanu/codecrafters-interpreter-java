package lox;

import java.util.Objects;

public class ParseException extends LoxException {
  private Span span;

  public ParseException(Span span) {
    this(null, span);
  }

  public ParseException(String message, Span span) {
    super(message);
    Objects.nonNull(span);
    this.span = span;
  }

  public Span getSpan() {
    return span;
  }

  @Override
  public String toString() {
    return "[line " + getSpan().line() + "] Error: " + super.toString();
  }
}
