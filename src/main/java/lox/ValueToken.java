package lox;

interface ValueToken<T> extends Token {
  T value();
}
