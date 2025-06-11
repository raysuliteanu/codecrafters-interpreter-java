package lox.token;

public interface ValueToken<T> extends Token {
  T value();
}
