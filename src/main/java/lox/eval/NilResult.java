package lox.eval;

public class NilResult implements EvaluationResult<Void> {
    @Override
    public Void value() {
        return null;
    }

    @Override
    public String toString() {
        return "nil";
    }
}
