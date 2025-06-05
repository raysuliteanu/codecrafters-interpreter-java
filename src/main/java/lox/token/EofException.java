package lox.token;

public class EofException extends ParseException {

    public EofException(lox.Span span) {
        this("Unexpected EOF", span);
    }

    public EofException(String message, lox.Span span) {
        super(message, span);
    }
}
