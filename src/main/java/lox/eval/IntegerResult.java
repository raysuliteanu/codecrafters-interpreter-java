package lox.eval;

import lox.token.IntegerToken;

public class IntegerResult implements EvaluationResult<Integer> {
    private final Integer value;
    private final String original;

    public IntegerResult(IntegerToken token) {
        this.value = token.value();
        this.original = token.original();
    }

    public IntegerResult(Integer value) {
        this.value = value;
        this.original = value.toString();
    }

    public IntegerResult(String value) {
        this.value = Integer.parseInt(value);
        this.original = value;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public String toString() {
        return original;
    }
}
