package lox.eval;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

import lox.NotImplementedException;
import lox.Result;
import lox.parse.Ast;
import lox.parse.Expr;
import lox.parse.Parser;
import lox.token.Token;
import lox.token.Tokens.Lexemes;
import lox.token.ValueToken;
import lox.token.NumberToken;
import lox.token.StringToken;
import lox.util.LogUtil;

public class Interpreter {
    public Result<Optional<EvaluationResult>, List<Throwable>> evaluate(final CharSequence source) {
        LogUtil.trace("evaluate");
        var parser = new Parser(source).parse();

        if (parser.hasErr()) {
            return new Result<>(null, parser.error());
        }

        var errors = new ArrayList<Throwable>();

        Optional<EvaluationResult> result = eval(parser.success(), errors);

        return new Result<>(result, errors);
    }

    private Optional<EvaluationResult> eval(final List<Ast> tree, final List<Throwable> errors) {
        LogUtil.trace("eval");
        var gatherer = Gatherers.<Ast, EvalState>fold(EvalState::new, (state, ast) -> {
            state.set(evalAll(ast));
            return state;
        });

        return tree.stream()
                .gather(gatherer)
                .map((state) -> state.get())
                .findFirst();
    }

    private EvaluationResult evalAll(Ast ast) {
        LogUtil.trace("evalAll");
        return switch (ast) {
            case Expr.Terminal t -> evalTerminal(t);
            default -> null; // TODO:
        };
    }

    private EvaluationResult evalTerminal(final Expr.Terminal terminal) {
        LogUtil.trace("evalTerminal");
        Token token = terminal.token();
        Lexemes lexeme = token.lexeme();
        return switch (lexeme) {
            case NUMBER -> new NumberResult(((NumberToken) token).value());
            case STRING -> new StringResult(((StringToken) token).value());
            case TRUE -> new BooleanResult(true);
            case FALSE -> new BooleanResult(false);
            case NIL -> new NilResult();
            default -> throw new NotImplementedException(lexeme.toString());
        };
    }

}
