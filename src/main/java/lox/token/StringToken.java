package lox.token;

import lox.token.Tokens.Lexemes;

public class StringToken extends SpanningToken implements ValueToken<String> {

    private final String value;

    StringToken(String value) {
        super(Lexemes.STRING);
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return lexeme.name() + " " + "\"" + value + "\"" + " " + value;
    }
}
