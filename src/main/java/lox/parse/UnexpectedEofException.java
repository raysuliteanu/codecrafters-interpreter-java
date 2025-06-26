package lox.parse;

public class UnexpectedEofException extends ParseException {
    public UnexpectedEofException() {
        super("Unexpected EOF");
    }
}
