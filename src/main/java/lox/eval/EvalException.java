package lox.eval;

import lox.LoxException;

public class EvalException extends LoxException {
    public EvalException() {
    }

    public EvalException(String message) {
        super(message);
    }

    public EvalException(String message, Throwable cause) {
        super(message, cause);
    }

    public EvalException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
