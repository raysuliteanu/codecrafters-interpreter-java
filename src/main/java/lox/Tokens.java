package lox;

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
    LESS_EQ("<="),
    GREATER(">"),
    GREATER_EQ(">="),
    BANG("!"),
    BANG_EQ("!="),
    EQ("="),
    EQ_EQ("=="),

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

    public static boolean isKeyword(String val) {
      return val.equalsIgnoreCase(TRUE.name()) ||
          val.equalsIgnoreCase(Lexemes.FALSE.name()) ||
          val.equalsIgnoreCase(Lexemes.NIL.name()) ||
          val.equalsIgnoreCase(Lexemes.AND.name()) ||
          val.equalsIgnoreCase(Lexemes.OR.name()) ||
          val.equalsIgnoreCase(Lexemes.CLASS.name()) ||
          val.equalsIgnoreCase(Lexemes.FOR.name()) ||
          val.equalsIgnoreCase(Lexemes.FUN.name()) ||
          val.equalsIgnoreCase(Lexemes.IF.name()) ||
          val.equalsIgnoreCase(Lexemes.ELSE.name()) ||
          val.equalsIgnoreCase(Lexemes.RETURN.name()) ||
          val.equalsIgnoreCase(Lexemes.SUPER.name()) ||
          val.equalsIgnoreCase(Lexemes.THIS.name()) ||
          val.equalsIgnoreCase(Lexemes.WHILE.name()) ||
          val.equalsIgnoreCase(Lexemes.VAR.name()) ||
          val.equalsIgnoreCase(Lexemes.PRINT.name());
    }

    public String toString() {
      return name() + " " + lexeme();
    }
  }

  // TODO: make Object for value be a generic T
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
        case NUMBER -> new NumberToken(null);
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
      return "TokenBuilder [lexeme=" + lexeme + ", value=" + value + ", span=" + span + "]";
    }
  }

  public static TokenBuilder ofType(Lexemes lexeme) {
    return new TokenBuilder(lexeme);
  }
}
