package lox.eval;

public class NilResult implements EvaluationResult {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T value() {
        return (T) null;
    }

    @Override
    public String toString() {
        return "nil";
    }
}
