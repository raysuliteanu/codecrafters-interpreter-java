package lox.token;

import lox.Span;
import lox.token.Tokens.Lexemes;

public interface Token {
  Lexemes lexeme();

  Span span();
}
