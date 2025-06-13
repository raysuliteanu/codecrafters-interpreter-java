package lox.eval;

public class BooleanResult implements EvaluationResult {
    private final Boolean value;

    public BooleanResult(final Boolean value) {
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
