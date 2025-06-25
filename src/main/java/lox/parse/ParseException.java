package lox.parse;

import lox.LoxException;

public class ParseException extends LoxException {
    public ParseException(String msg) {
        super(msg);
    }

    public static int errorCode() {
        return 65;
    }
}
