package lox.token;

import lox.Span;

public interface Token {
    String lexeme();

    Span span();
}
