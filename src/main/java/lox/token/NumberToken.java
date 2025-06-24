package lox.token;

import lox.token.Tokens.Lexemes;

public abstract class NumberToken<T extends Number> extends SpanningToken implements ValueToken<T> {
    private final String original;
    private final T value;

    protected NumberToken(String original, T value) {
        super(Lexemes.NUMBER);
        this.value = value;
        this.original = original;
    }

    public T value() {
        return this.value;
    }

    public String original() {
        return original;
    }

    @Override
    public String toString() {
        return lexeme.name() + " " + original + " " + value;
    }
}
