package lox.eval;

import lox.token.DoubleToken;

public class DoubleResult implements EvaluationResult<Double> {
    private final Double value;
    private final String original;

    public DoubleResult(DoubleToken token) {
        this.value = token.value();
        this.original = token.original();
    }

    public DoubleResult(Double value) {
        this.value = value;
        this.original = value.toString();
    }

    public DoubleResult(String value) {
        this.value = Double.parseDouble(value);
        this.original = value;
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public String toString() {
        return original;
    }
}
