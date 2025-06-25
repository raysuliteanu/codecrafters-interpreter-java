package lox.parse;

public class MissingTokenException extends ParseException {
    public MissingTokenException(String expected, String actual) {
        super("missing token " + expected + " got " + actual);
    }
}
