package lox;

public class ValueToken<T> extends SpanningToken {
  private final T value;

  ValueToken(Tokens.Lexemes lexeme, T value) {
    super(lexeme);
    this.value = value;
  }

  public T value() {
    return this.value;
  }
}
