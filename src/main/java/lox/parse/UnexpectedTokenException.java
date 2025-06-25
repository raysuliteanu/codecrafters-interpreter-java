package lox.parse;

public class UnexpectedTokenException extends ParseException {
    public UnexpectedTokenException(String token) {
        super("Unexpected token " + token);
    }
}
