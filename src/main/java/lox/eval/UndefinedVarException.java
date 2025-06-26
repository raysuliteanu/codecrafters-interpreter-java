package lox.eval;

import lox.LoxException;

public class UndefinedVarException extends EvalException {
    public UndefinedVarException() {
    }

    public UndefinedVarException(String var) {
        super("Undefined variable '" + var + "'.");
    }
}
