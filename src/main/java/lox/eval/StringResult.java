package lox.eval;

public class StringResult implements EvaluationResult {
    private final CharSequence value;

    public StringResult(final CharSequence value) {
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
