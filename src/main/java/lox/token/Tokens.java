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

        Lexemes(String lexeme) {
            this.lexeme = lexeme;
        }

        Lexemes() {
            this.lexeme = null;
        }

        public String value() {
            return lexeme;
        }

        public static boolean isStmt(String val) {
            return (val.equals(IF.value()) ||
                    val.equals(Lexemes.FOR.value()) ||
                    val.equals(Lexemes.WHILE.value()) ||
                    val.equals(Lexemes.PRINT.value()) ||
                    val.equals(Lexemes.RETURN.value()) ||
                    // left brace '{' is "start of statement(s)"
                    val.equals(Lexemes.LEFT_BRACE.value()));

        }

        public static boolean isKeyword(String val) {
            return (val.equals(TRUE.value()) ||
                    val.equals(Lexemes.FALSE.value()) ||
                    val.equals(Lexemes.NIL.value()) ||
                    val.equals(Lexemes.AND.value()) ||
                    val.equals(Lexemes.OR.value()) ||
                    val.equals(Lexemes.CLASS.value()) ||
                    val.equals(Lexemes.FOR.value()) ||
                    val.equals(Lexemes.FUN.value()) ||
                    val.equals(Lexemes.IF.value()) ||
                    val.equals(Lexemes.ELSE.value()) ||
                    val.equals(Lexemes.RETURN.value()) ||
                    val.equals(Lexemes.SUPER.value()) ||
                    val.equals(Lexemes.THIS.value()) ||
                    val.equals(Lexemes.WHILE.value()) ||
                    val.equals(Lexemes.VAR.value()) ||
                    val.equals(Lexemes.PRINT.value()));
        }

        public String toString() {
            return name() + " " + value();
        }
    }

    public static class TokenBuilder {

        private final Lexemes lexeme;
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
                case NUMBER -> {
                    yield new DoubleToken((String) value);
                }
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
