package lox.token;

import java.util.Objects;

public class ParseException extends lox.LoxException {

    private lox.Span span;

    public ParseException(lox.Span span) {
        this(null, span);
    }

    public ParseException(String message, lox.Span span) {
        super(message);
        Objects.nonNull(span);
        this.span = span;
    }

    public lox.Span getSpan() {
        return span;
    }

    @Override
    public String toString() {
        return "[line " + getSpan().line() + "] Error: " + super.toString();
    }
}
