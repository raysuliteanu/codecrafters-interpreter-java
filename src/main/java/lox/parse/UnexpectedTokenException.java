package lox.parse;

import lox.LoxException;

public class UnexpectedTokenException extends LoxException {
    public UnexpectedTokenException(String token) {
        super("Unexpected token " + token);
    }
}
