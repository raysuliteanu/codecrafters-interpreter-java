package lox.token;

public class UnterminatedStringException extends ParseException {

    String msg;

    public UnterminatedStringException(String msg, lox.Span span) {
        super("Unterminated string", span);
        this.msg = msg;
    }

    @Override
    public String toString() {
        return super.toString() + ".";
    }
}
