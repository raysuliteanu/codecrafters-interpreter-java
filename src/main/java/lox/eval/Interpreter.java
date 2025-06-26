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
import lox.parse.ParseException;
import lox.parse.Stmt;
import lox.parse.Ast.Block;
import lox.parse.Ast.Var;
import lox.token.DoubleToken;
import lox.token.Token;
import lox.token.IdentifierToken;
import lox.token.Tokens.Lexemes;
import lox.token.StringToken;
import lox.token.ValueToken;

import static lox.util.LogUtil.trace;

public class Interpreter {
    private final boolean expressionMode;
    private final EvalState state = new EvalState();

    public Interpreter(boolean expressionMode) {
        this.expressionMode = expressionMode;
    }

    public Result<Optional<EvaluationResult<?>>, List<Throwable>> evaluate(final CharSequence source) {
        trace("evaluate: " + source);

        var parser = new Parser(source, this.expressionMode).parse();

        if (parser.hasErr()) {
            return new Result<>(null, parser.error());
        }

        var errors = new ArrayList<Throwable>();

        Optional<EvaluationResult<?>> result = eval(parser.success(), errors);

        return new Result<>(result, errors);
    }

    private Optional<EvaluationResult<?>> eval(final List<Ast> tree, final List<Throwable> errors) {
        trace("eval");
        EvaluationResult result = null;
        for (var ast : tree) {
            try {
                result = evalAst(ast);
            } catch (ParseException e) {
                errors.add(e);
            } catch (EvalException e) {
                errors.add(e);
                break;
            }
        }

        return Optional.ofNullable(result);
    }

    private EvaluationResult<?> evalAst(final Ast ast) {
        trace("evalAst");
        return switch (ast) {
            case Stmt s -> evalStatement(s);
            case Expr e -> evalExpr(e);
            case Var v -> evalVarDecl(v);
            case Block b -> evalBlock(b);
            default -> throw new NotImplementedException(ast.toString());
        };
    }

    private EvaluationResult<?> evalBlock(final Block block) {
        trace("evalBlock");
        this.state.push();
        EvaluationResult result = null;
        try {
            for (var s : block.block()) {
                result = evalAst(s);
            }
        } finally {
            this.state.pop();
        }

        return result;
    }

    private EvaluationResult<?> evalStatement(final Stmt ast) {
        trace("evalStmt");
        return switch (ast) {
            case Stmt.PrintStmt p -> printStmt(p);
            case Stmt.ExprStmt e -> exprStmt(e);
            case Stmt.IfStmt i -> ifStmt(i);
            case Stmt.ForStmt s -> throw new NotImplementedException(s.toString());
            case Stmt.WhileStmt s -> throw new NotImplementedException(s.toString());
            case Stmt.ReturnStmt s -> throw new NotImplementedException(s.toString());
        };
    }

    private EvaluationResult<?> ifStmt(Stmt.IfStmt ast) {
        trace("ifStmt");
        var cond = evalExpr(ast.condition());
        if (cond instanceof BooleanResult br) {
            trace("if cond is " + cond);
            if (br.value()) {
                trace("then branch");
                return switch (ast.thenStmt()) {
                    case Stmt s -> evalStatement(s);
                    case Ast.Block b -> evalBlock(b);
                    default -> throw new EvalException("todo: then block not stmt or block");
                };
            }

            if (ast.elseStmt().isPresent()) {
                trace("else branch");
                var elseStmt = ast.elseStmt().get();
                return switch (elseStmt) {
                    case Stmt s -> evalStatement(s);
                    case Ast.Block b -> evalBlock(b);
                    default -> throw new EvalException("todo: else block not stmt or block");
                };
            }

            return null;
        }

        throw new EvalException(cond.getClass().getName() + ": " + cond);
    }

    private EvaluationResult<?> printStmt(Stmt.PrintStmt ast) {
        System.out.println(evalExpr(ast.expr()));
        return null;
    }

    private EvaluationResult<?> exprStmt(Stmt.ExprStmt ast) {
        return evalExpr(ast.expr());
    }

    private EvaluationResult<?> evalExpr(Expr ast) {
        trace("evalExpr");
        return switch (ast) {
            case Expr.Terminal t -> evalTerminal(t);
            case Expr.Group g -> evalExpr(g.group());
            case Expr.Unary u -> evalUnary(u);
            case Expr.Binary b -> evalBinary(b);
            case Expr.Assignment a -> evalAssignment(a);
        };
    }

    private EvaluationResult<?> evalAssignment(final Expr.Assignment assignment) {
        var id = ((IdentifierToken) assignment.identifier()).value();
        var val = evalExpr(assignment.expression());

        this.state.updateVariable(id, val);

        return val;
    }

    private EvaluationResult<?> evalUnary(final Expr.Unary unary) {
        trace("evalUnary");
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
        trace("evalBinary");
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
        trace("evalTerminal");
        Token token = terminal.token();
        Lexemes lexeme = token.lexeme();
        var result = switch (lexeme) {
            case NUMBER -> new DoubleResult(((DoubleToken) token).value());
            case STRING -> new StringResult(((StringToken) token).value());
            case TRUE -> new BooleanResult(true);
            case FALSE -> new BooleanResult(false);
            case NIL -> new NilResult();
            case IDENTIFIER -> {
                var id = ((IdentifierToken) token).value();
                yield state.variable(id);
            }
            default -> throw new NotImplementedException(lexeme.toString());
        };
        trace("evalTerminal result: " + result);
        return result;
    }

    private EvaluationResult<?> evalVarDecl(Ast.Var varDecl) {
        trace("evalVarDecl: " + varDecl.identifier());
        Optional<Expr> initializer = varDecl.initializer();
        state.addVariable(((IdentifierToken) varDecl.identifier()).value(),
                initializer.isPresent() ? evalExpr(initializer.get()) : null);

        return null;
    }
}
