package lox;

import lox.Tokens.Lexemes;

public class NumberToken extends SpanningToken implements ValueToken<Number> {
  private final Number value;

  NumberToken(Number value) {
    super(Lexemes.NUMBER);
    this.value = value;
  }

  public Number value() {
    return this.value;
  }

  @Override
  public String toString() {
    return lexeme.name() + " " + "\"" + value + "\"" + " " + value;
  }
}
