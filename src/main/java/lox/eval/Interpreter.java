package lox.eval;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.List;

import lox.LoxException;
import lox.NotImplementedException;
import lox.Result;
import lox.parse.Ast;
import lox.parse.Expr;
import lox.parse.Parser;
import lox.parse.Stmt;
import lox.token.DoubleToken;
import lox.token.Token;
import lox.token.Tokens.Lexemes;
import lox.token.StringToken;
import lox.token.ValueToken;
import lox.util.LogUtil;

public class Interpreter {
    private final boolean expressionMode;

    public Interpreter(boolean expressionMode) {
        this.expressionMode = expressionMode;
    }

    public Result<Optional<EvaluationResult<?>>, List<Throwable>> evaluate(final CharSequence source) {
        LogUtil.trace("evaluate: " + source);

        var parser = new Parser(source, this.expressionMode).parse();

        if (parser.hasErr()) {
            return new Result<>(null, parser.error());
        }

        var errors = new ArrayList<Throwable>();

        Optional<EvaluationResult<?>> result = eval(parser.success(), errors);

        return new Result<>(result, errors);
    }

    private Optional<EvaluationResult<?>> eval(final List<Ast> tree, final List<Throwable> errors) {
        LogUtil.trace("eval");
        EvaluationResult result = null;
        for (var ast : tree) {
            try {
                result = switch (ast) {
                    case Stmt s -> evalStatement(s);
                    case Expr e -> evalExpr(e);
                    default -> throw new NotImplementedException(ast.toString());
                };
            } catch (LoxException e) {
                errors.add(e);
            }
        }

        return Optional.ofNullable(result);
    }

    private EvaluationResult<?> evalStatement(Stmt ast) {
        LogUtil.trace("evalStmt");
        return switch (ast) {
            case Stmt.PrintStmt s -> printStmt(s);
            case Stmt.IfStmt s -> throw new NotImplementedException(s.toString());
            case Stmt.ForStmt s -> throw new NotImplementedException(s.toString());
            case Stmt.WhileStmt s -> throw new NotImplementedException(s.toString());
            case Stmt.ExprStmt s -> throw new NotImplementedException(s.toString());
            case Stmt.ReturnStmt s -> throw new NotImplementedException(s.toString());
        };
    }

    private EvaluationResult<?> printStmt(Stmt.PrintStmt ast) {
        System.out.println(evalExpr(ast.expr()));
        return null;
    }

    private EvaluationResult<?> evalExpr(Expr ast) {
        LogUtil.trace("evalExpr");
        return switch (ast) {
            case Expr.Terminal t -> evalTerminal(t);
            case Expr.Group g -> evalExpr(g.group());
            case Expr.Unary u -> evalUnary(u);
            case Expr.Binary b -> evalBinary(b);
        };
    }

    private EvaluationResult<?> evalUnary(final Expr.Unary unary) {
        LogUtil.trace("evalUnary");
        EvaluationResult<?> e = evalExpr(unary.expr());
        var token = unary.token();
        var lexeme = token.lexeme();
        return switch (lexeme) {
            case Lexemes.BANG -> {
                switch (e) {
                    case BooleanResult br -> {
                        // e.g. !true
                        yield new BooleanResult(!(boolean) br.value());
                    }
                    case DoubleResult nr -> {
                        // e.g. !10
                        yield new BooleanResult(false);
                    }
                    case NilResult nr -> {
                        // e.g. !nil
                        yield new BooleanResult(true);
                    }
                    case null, default -> throw new EvalException("invalid operation " + lexeme.value() + " for " + e);
                }
            }
            case Lexemes.MINUS -> {
                if (e instanceof DoubleResult nr) {
                    yield new DoubleResult(-nr.value());
                } else {
                    throw new EvalException("invalid operation " + lexeme.value() + " for " + e);
                }
            }
            default -> throw new EvalException("invalid operation " + lexeme.value() + " for " + e);
        };
    }

    private EvaluationResult<?> evalBinary(final Expr.Binary binary) {
        LogUtil.trace("evalBinary");
        EvaluationResult<?> left = evalExpr(binary.left());
        EvaluationResult<?> right = evalExpr(binary.right());
        var lexeme = binary.op().lexeme();
        return switch (lexeme) {
            case Lexemes.PLUS -> {
                if (left instanceof StringResult lr && right instanceof StringResult rr) {
                    // string concatenation
                    yield new StringResult(lr.value().toString() + rr.value());
                } else if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    // number addition
                    yield new DoubleResult(lr.value() + rr.value());
                } else {
                    throw new EvalException("Operands must be two numbers or two strings.");
                }
            }
            case Lexemes.MINUS -> {
                // e.g. 1 - 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    // number addition
                    yield new DoubleResult(lr.value() - rr.value());
                } else {
                    throw new EvalException("invalid operation " + lexeme);
                }
            }
            case Lexemes.STAR -> {
                // e.g. 1 * 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    // number addition
                    yield new DoubleResult(lr.value() * rr.value());
                } else {
                    throw new EvalException("invalid operation " + lexeme);
                }
            }
            case Lexemes.SLASH -> {
                // e.g. 1 / 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    // number addition
                    yield new DoubleResult(lr.value() / rr.value());
                } else {
                    throw new EvalException("invalid operation " + lexeme);
                }
            }
            case Lexemes.EQUAL_EQUAL -> {
                // e.g. 1 == 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    yield new BooleanResult(Objects.equals(lr.value(), rr.value()));
                } else if (left instanceof StringResult lr && right instanceof StringResult rr) {
                    yield new BooleanResult(Objects.equals(lr.value(), rr.value()));
                } else if (left instanceof BooleanResult lr && right instanceof BooleanResult rr) {
                    yield new BooleanResult(Objects.equals(lr.value(), rr.value()));
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.BANG_EQUAL -> {
                // e.g. 1 != 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    yield new BooleanResult(!Objects.equals(lr.value(), rr.value()));
                } else if (left instanceof StringResult lr && right instanceof StringResult rr) {
                    yield new BooleanResult(!Objects.equals(lr.value(), rr.value()));
                } else if (left instanceof BooleanResult lr && right instanceof BooleanResult rr) {
                    yield new BooleanResult(!Objects.equals(lr.value(), rr.value()));
                } else {
                    yield new BooleanResult(false);
                }
            }
            case Lexemes.LESS -> {
                // e.g. 1 < 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    yield new BooleanResult(lr.value() < rr.value());
                } else {
                    throw new EvalException("Operands must be numbers.");
                }
            }
            case Lexemes.LESS_EQUAL -> {
                // e.g. 1 <= 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    yield new BooleanResult(lr.value() <= rr.value());
                } else {
                    throw new EvalException("Operands must be numbers.");
                }
            }
            case Lexemes.GREATER -> {
                // e.g. 1 > 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    yield new BooleanResult(lr.value() > rr.value());
                } else {
                    throw new EvalException("Operands must be numbers.");
                }
            }
            case Lexemes.GREATER_EQUAL -> {
                // e.g. 1 >= 2
                if (left instanceof DoubleResult lr && right instanceof DoubleResult rr) {
                    yield new BooleanResult(lr.value() >= rr.value());
                } else {
                    throw new EvalException("Operands must be numbers.");
                }
            }
            default -> throw new EvalException("invalid operation " + lexeme);
        };
    }

    private EvaluationResult<?> evalTerminal(final Expr.Terminal terminal) {
        LogUtil.trace("evalTerminal");
        Token token = terminal.token();
        Lexemes lexeme = token.lexeme();
        var result = switch (lexeme) {
            case NUMBER -> {
                yield new DoubleResult(((DoubleToken) token).value());
            }
            case STRING -> new StringResult(((StringToken) token).value());
            case TRUE -> new BooleanResult(true);
            case FALSE -> new BooleanResult(false);
            case NIL -> new NilResult();
            default -> throw new NotImplementedException(lexeme.toString());
        };
        LogUtil.trace("evalTerminal result: " + result);
        return result;
    }

}
