package lox;

import lox.Tokens.Lexemes;

public class NumberToken extends SpanningToken implements ValueToken<Number> {
  private final String original;
  private final Number value;

  NumberToken(String original) {
    super(Lexemes.NUMBER);
    this.original = original;

    String tmp = original;
    if (original.endsWith(".")) {
      tmp = original.substring(0, original.lastIndexOf("."));
    }
    this.value = Double.parseDouble(tmp);
  }

  public Number value() {
    return this.value;
  }

  @Override
  public String toString() {
    return lexeme.name() + " " + original + " " + value;
  }
}
