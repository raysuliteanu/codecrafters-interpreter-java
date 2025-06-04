package lox;

import lox.Tokens.Lexemes;

public class IdentifierToken extends SpanningToken implements ValueToken<String> {
  private final String value;

  IdentifierToken(String value) {
    super(Lexemes.IDENTIFIER);
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  @Override
  public String toString() {
    return lexeme.name() + " " + value + " null";
  }
}
