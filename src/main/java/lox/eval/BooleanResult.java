package lox.eval;

public class BooleanResult implements EvaluationResult<Boolean> {
    private final Boolean value;

    public BooleanResult(final Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
