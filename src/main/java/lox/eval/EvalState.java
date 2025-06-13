package lox.eval;

public class EvalState {
    private EvaluationResult value;

    public EvalState() {
    }

    public EvalState(final EvaluationResult value) {
        this.value = value;
    }

    public void set(final EvaluationResult value) {
        this.value = value;
    }

    public EvaluationResult get() {
        return value;
    }
}
