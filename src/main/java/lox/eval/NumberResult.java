package lox.eval;

public class NumberResult implements EvaluationResult {
    private final Number value;

    public NumberResult(Number value) {
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T value() {
        return (T) value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
