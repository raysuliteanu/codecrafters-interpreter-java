package lox.eval;

import lox.token.NumberToken;

public class NumberResult implements EvaluationResult {
    private final Double value;
    private final String original;

    public NumberResult(NumberToken token) {
        this.value = token.value();
        this.original = token.original();
    }

    public NumberResult(Double value) {
        this.value = value;
        this.original = value.toString();
    }

    public NumberResult(String value) {
        this.value = Double.parseDouble(value);
        this.original = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T value() {
        return (T) value;
    }

    @Override
    public String toString() {
        return original;
    }
}
