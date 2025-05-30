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

    // TODO: is this the right way to do this ...
    NUMBER(""),
    STRING(""),
    IDENTIFIER("");

    public final String lexeme;

    private Lexemes(String lexeme) {
      this.lexeme = lexeme;
    }

    public String lexeme() {
      return lexeme;
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
      Token t;
      if (value != null) {
        t = new ValueToken<>(lexeme, value);
      } else {
        t = new SpanningToken(lexeme);
      }

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
