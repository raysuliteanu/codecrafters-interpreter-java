package lox.token;

import lox.token.Tokens.Lexemes;

public class NumberToken extends SpanningToken implements ValueToken<Number> {

    private final String original;
    private final Number value;

    NumberToken(String original) {
        super(Lexemes.NUMBER);
        this.original = original;

        String tmp = original;
        if (original.endsWith(".")) {
            tmp = original + "0";
        }
        this.value = Double.parseDouble(tmp);
    }

    public Number value() {
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
