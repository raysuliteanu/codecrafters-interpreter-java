package lox.eval;

import lox.token.DoubleToken;

public class DoubleResult implements EvaluationResult<Double> {
    private final Double value;
    private final String original;

    public DoubleResult(Double value) {
        this.value = value;
        this.original = value.toString();
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public String toString() {
        return original.endsWith(".0") ? original.substring(0, original.length() - 2) : original;
    }
}
