package lox.eval;

public class StringResult implements EvaluationResult<CharSequence> {
    private final CharSequence value;

    public StringResult(final CharSequence value) {
        this.value = value;
    }

    @Override
    public CharSequence value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
