package lox.eval;

import java.util.Map;
import java.util.Optional;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class EvalState {
    private final Deque<Map<String, EvaluationResult<?>>> state;

    public EvalState() {
        state = new ArrayDeque<>();
        state.addFirst(new HashMap<>());
    }

    public void push() {
        state.addFirst(new HashMap<>());
    }

    public void pop() {
        state.removeFirst();
    }

    public EvaluationResult<?> variable(final String varName) {
        for (var context : state) {
            if (context.containsKey(varName)) {
                return context.get(varName);
            }
        }

        throw new UndefinedVarException(varName);
    }

    public void addVariable(final String name, final EvaluationResult<?> value) {
        state.getFirst().put(name, value != null ? value : new NilResult());
    }
}
