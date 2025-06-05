package lox.token;

import lox.Span;

public class SpanningToken extends AbstractToken {

    private Span span;

    SpanningToken(Tokens.Lexemes lexeme) {
        this(lexeme, null);
    }

    SpanningToken(Tokens.Lexemes lexeme, Span span) {
        super(lexeme);
        this.span = span;
    }

    @Override
    public Span span() {
        return span;
    }

    Token withSpan(Span span) {
        this.span = span;
        return this;
    }
}
