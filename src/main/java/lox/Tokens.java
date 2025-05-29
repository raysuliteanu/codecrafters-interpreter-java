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
    LESS("<"),
    LESS_EQ("<="),
    GREATER(">"),
    GREATER_EQ(">="),
    BANG("!"),
    BANG_EQ("!="),
    EQ("="),
    EQ_EQ("=="),
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
  }

  public static TokenBuilder ofType(Lexemes lexeme) {
    return new TokenBuilder(lexeme);
  }

  //
  // public static class LeftParen extends AbstractToken {
  // LeftParen() {
  // super("(");
  // }
  //
  // @Override
  // public String toString() {
  // return "LEFT_PAREN " + super.toString();
  // }
  // }
  //
  // public static class RightParen extends AbstractToken {
  // RightParen() {
  // super(")");
  // }
  //
  // @Override
  // public String toString() {
  // return "RIGHT_PAREN " + super.toString();
  // }
  // }
  //
  // public static class LeftBrace extends AbstractToken {
  // LeftBrace() {
  // super("{");
  // }
  //
  // @Override
  // public String toString() {
  // return "LEFT_BRACE " + super.toString();
  // }
  // }
  //
  // public static class RightBrace extends AbstractToken {
  // RightBrace() {
  // super("}");
  // }
  //
  // @Override
  // public String toString() {
  // return "RIGHT_BRACE " + super.toString();
  // }
  // }
  //
  // public static class Comma extends AbstractToken {
  // Comma() {
  // super(",");
  // }
  //
  // @Override
  // public String toString() {
  // return "COMMA " + super.toString();
  // }
  // }
  //
  // public static class Dot extends AbstractToken {
  // Dot() {
  // super(".");
  // }
  //
  // @Override
  // public String toString() {
  // return "DOT " + super.toString();
  // }
  // }
  //
  // public static class Minus extends AbstractToken {
  // Minus() {
  // super("-");
  // }
  //
  // @Override
  // public String toString() {
  // return "MINUS " + super.toString();
  // }
  // }
  //
  // public static class Plus extends AbstractToken {
  // Plus() {
  // super("+");
  // }
  //
  // @Override
  // public String toString() {
  // return "PLUS " + super.toString();
  // }
  // }
  //
  // public static class Semicolon extends AbstractToken {
  // Semicolon() {
  // super(";");
  // }
  //
  // @Override
  // public String toString() {
  // return "SEMICOLON " + super.toString();
  // }
  // }
  //
  // public static class Star extends AbstractToken {
  // Star() {
  // super("*");
  // }
  //
  // @Override
  // public String toString() {
  // return "STAR " + super.toString();
  // }
  // }
  // Eq,
  // EqEq,
  // Bang,
  // BangEq,
  // Less,
  // LessEq,
  // Greater,
  // GreaterEq,
  // Slash,
  //
  // // keywords
  // True,
  // False,
  // Nil,
  // And,
  // Or,
  // Class,
  // For,
  // Fun,
  // If,
  // Else,
  // Return,
  // Super,
  // This,
  // Var,
  // While,
  // Print,
  //
  //
  // // lexemes with values
  // Number,
  // Identifier,
  // String,
  //
  // // symbolic placeholder
  // Eof,
}
