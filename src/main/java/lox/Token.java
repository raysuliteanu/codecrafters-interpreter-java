package lox;

interface Token {
  String lexeme();

  Span span();

  default Object value() {
    return "null";
  };
}
