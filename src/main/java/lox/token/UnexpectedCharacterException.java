package lox.token;

public class UnexpectedCharacterException extends ParseException {

    final Character actual;

    public UnexpectedCharacterException(Character actual, lox.Span span) {
        super("Unexpected character", span);
        this.actual = actual;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + actual;
    }
}
