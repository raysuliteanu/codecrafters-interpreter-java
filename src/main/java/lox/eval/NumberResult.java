package lox.eval;

import lox.token.NumberToken;

public class NumberResult implements EvaluationResult {
    private final NumberToken value;

    public NumberResult(NumberToken value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T value() {
        return (T) value.value();
    }

    @Override
    public String toString() {
        return value.original();
    }
}
