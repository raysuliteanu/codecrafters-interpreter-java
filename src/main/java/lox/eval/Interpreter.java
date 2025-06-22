package lox.eval;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

import lox.LoxException;
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
        EvalState state = new EvalState();
        tree.forEach((ast) -> {
            try {
                state.set(evalAll(ast));
            } catch (LoxException e) {
                errors.add(e);
            }
        });
        return Optional.ofNullable(state.get());
    }

    private EvaluationResult evalAll(Ast ast) {
        LogUtil.trace("evalAll");
        return switch (ast) {
            case Expr.Terminal t -> evalTerminal(t);
            case Expr.Group g -> evalAll(g.group());
            case Expr.Unary u -> evalUnary(u);
            case Expr.Binary b -> evalBinary(b);
            default -> null; // TODO:
        };
    }

    private EvaluationResult evalUnary(final Expr.Unary unary) {
        LogUtil.trace("evalUnary");
        EvaluationResult e = evalAll(unary.expr());
        var token = unary.token();
        var lexeme = token.lexeme();
        return switch (lexeme) {
            case Lexemes.BANG -> {
                if (e instanceof BooleanResult br) {
                    // e.g. !true
                    yield new BooleanResult(!(boolean) br.value());
                } else if (e instanceof NumberResult nr) {
                    // e.g. !10
                    yield new BooleanResult(false);
                } else if (e instanceof NilResult nr) {
                    // e.g. !nil
                    yield new BooleanResult(true);
                } else {
                    throw new EvalException("invalid operation " + lexeme.value() + " for " + e);
                }
            }
            case Lexemes.MINUS -> {
                if (e instanceof NumberResult nr) {
                    yield new NumberResult("-" + nr.toString());
                } else {
                    throw new EvalException("invalid operation " + lexeme.value() + " for " + e);
                }
            }
            default -> {
                throw new EvalException("invalid operation " + lexeme.value() + " for " + e);
            }
        };
    }

    private EvaluationResult evalBinary(final Expr.Binary binary) {
        LogUtil.trace("evalBinary");
        EvaluationResult left = evalAll(binary.left());
        EvaluationResult right = evalAll(binary.right());
        var lexeme = binary.op().lexeme();
        return switch (lexeme) {
            case Lexemes.PLUS -> {
                if (left instanceof StringResult && right instanceof StringResult) {
                    // string concatenation
                    yield new StringResult((String) left.value() + (String) right.value());
                } else if (left instanceof NumberResult && right instanceof NumberResult) {
                    // number addition
                    yield new NumberResult((Double) left.value() + (Double) right.value());
                } else {
                    throw new EvalException("invalid operation " + lexeme);
                }
            }
            case Lexemes.MINUS -> {
                // e.g. 1 - 2
                yield new NumberResult((Double) left.value() - (Double) right.value());
            }
            case Lexemes.STAR -> {
                // e.g. 1 * 2
                yield new NumberResult((Double) left.value() * (Double) right.value());
            }
            case Lexemes.SLASH -> {
                // e.g. 1 / 2
                yield new NumberResult((Double) left.value() / (Double) right.value());
            }
            case Lexemes.EQUAL_EQUAL -> {
                // e.g. 1 == 2
                if (left instanceof NumberResult && right instanceof NumberResult) {
                    yield new BooleanResult((Double) left.value() == (Double) right.value());
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.LESS -> {
                // e.g. 1 < 2
                if (left instanceof NumberResult && right instanceof NumberResult) {
                    yield new BooleanResult((Double) left.value() < (Double) right.value());
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.LESS_EQUAL -> {
                // e.g. 1 <= 2
                if (left instanceof NumberResult && right instanceof NumberResult) {
                    yield new BooleanResult((Double) left.value() <= (Double) right.value());
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.GREATER -> {
                // e.g. 1 > 2
                if (left instanceof NumberResult && right instanceof NumberResult) {
                    yield new BooleanResult((Double) left.value() > (Double) right.value());
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.GREATER_EQUAL -> {
                // e.g. 1 >= 2
                if (left instanceof NumberResult && right instanceof NumberResult) {
                    yield new BooleanResult((Double) left.value() >= (Double) right.value());
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.BANG_EQUAL -> {
                // e.g. 1 != 2
                if (left instanceof NumberResult && right instanceof NumberResult) {
                    yield new BooleanResult((Double) left.value() != (Double) right.value());
                } else {
                    yield new BooleanResult(false);
                }
            }
            default -> {
                throw new EvalException("invalid operation " + lexeme);
            }
        };
    }

    private EvaluationResult evalTerminal(final Expr.Terminal terminal) {
        LogUtil.trace("evalTerminal");
        Token token = terminal.token();
        Lexemes lexeme = token.lexeme();
        return switch (lexeme) {
            case NUMBER -> new NumberResult(((NumberToken) token));
            case STRING -> new StringResult(((StringToken) token).value());
            case TRUE -> new BooleanResult(true);
            case FALSE -> new BooleanResult(false);
            case NIL -> new NilResult();
            default -> throw new NotImplementedException(lexeme.toString());
        };
    }

}
