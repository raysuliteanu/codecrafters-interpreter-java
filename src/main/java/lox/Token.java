package lox;

interface Token {
  String lexeme();

  Span span();
}
