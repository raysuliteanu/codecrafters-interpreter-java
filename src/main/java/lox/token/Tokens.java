package lox.token;

import lox.Span;

public class Tokens {

  public enum Lexemes {
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    STAR("*"),
    SEMICOLON(";"),
    MINUS("-"),
    PLUS("+"),
    DOT("."),
    COMMA(","),
    SLASH("/"),
    LESS("<"),
    LESS_EQUAL("<="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    BANG("!"),
    BANG_EQUAL("!="),
    EQUAL("="),
    EQUAL_EQUAL("=="),

    // value tokens
    NUMBER,
    STRING,
    IDENTIFIER,

    // keywords
    TRUE("true"),
    FALSE("false"),
    NIL("nil"),
    AND("and"),
    OR("or"),
    CLASS("class"),
    FOR("for"),
    FUN("fun"),
    IF("if"),
    ELSE("else"),
    RETURN("return"),
    SUPER("super"),
    THIS("this"),
    VAR("var"),
    WHILE("while"),
    PRINT("print");

    public final String lexeme;

    private Lexemes(String lexeme) {
      this.lexeme = lexeme;
    }

    private Lexemes() {
      this.lexeme = null;
    }

    public String lexeme() {
      return lexeme;
    }

    public static boolean isStmt(String val) {
      return (val.equals(IF.lexeme()) ||
          val.equals(Lexemes.FOR.lexeme()) ||
          val.equals(Lexemes.WHILE.lexeme()) ||
          val.equals(Lexemes.PRINT.lexeme()) ||
          val.equals(Lexemes.RETURN.lexeme()) ||
          // left brace '{' is "start of statement(s)"
          val.equals(Lexemes.LEFT_BRACE.lexeme()));

    }

    public static boolean isKeyword(String val) {
      return (val.equals(TRUE.lexeme()) ||
          val.equals(Lexemes.FALSE.lexeme()) ||
          val.equals(Lexemes.NIL.lexeme()) ||
          val.equals(Lexemes.AND.lexeme()) ||
          val.equals(Lexemes.OR.lexeme()) ||
          val.equals(Lexemes.CLASS.lexeme()) ||
          val.equals(Lexemes.FOR.lexeme()) ||
          val.equals(Lexemes.FUN.lexeme()) ||
          val.equals(Lexemes.IF.lexeme()) ||
          val.equals(Lexemes.ELSE.lexeme()) ||
          val.equals(Lexemes.RETURN.lexeme()) ||
          val.equals(Lexemes.SUPER.lexeme()) ||
          val.equals(Lexemes.THIS.lexeme()) ||
          val.equals(Lexemes.WHILE.lexeme()) ||
          val.equals(Lexemes.VAR.lexeme()) ||
          val.equals(Lexemes.PRINT.lexeme()));
    }

    public String toString() {
      return name() + " " + lexeme();
    }
  }

  public static class TokenBuilder {

    private Lexemes lexeme;
    private Object value;
    private Span span;

    TokenBuilder(Lexemes lexeme) {
      this.lexeme = lexeme;
    }

    public TokenBuilder withValue(Object value) {
      this.value = value;
      return this;
    }

    public TokenBuilder withSpan(long line, long offset, long length) {
      return this.withSpan(Span.of(line, offset, length));
    }

    public TokenBuilder withSpan(Span span) {
      this.span = span;
      return this;
    }

    public Token build() {
      Token t = switch (lexeme) {
        case STRING -> new StringToken((String) value);
        case NUMBER -> new NumberToken((String) value);
        case IDENTIFIER -> new IdentifierToken((String) value);
        default -> new SpanningToken(lexeme);
      };

      if (span != null) {
        ((SpanningToken) t).withSpan(span);
      }

      return t;
    }

    @Override
    public String toString() {
      return ("TokenBuilder [lexeme=" +
          lexeme +
          ", value=" +
          value +
          ", span=" +
          span +
          "]");
    }
  }

  public static TokenBuilder ofType(Lexemes lexeme) {
    return new TokenBuilder(lexeme);
  }
}
