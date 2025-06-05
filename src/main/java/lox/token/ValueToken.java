package lox.token;

interface ValueToken<T> extends Token {
    T value();
}
