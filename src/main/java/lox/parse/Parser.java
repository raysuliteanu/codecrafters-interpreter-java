package lox.parse;

import static lox.util.LogUtil.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lox.LoxException;
import lox.NotImplementedException;
import lox.Result;
import lox.parse.Ast.Var;
import lox.token.IdentifierToken;
import lox.token.Scanner;
import lox.token.Token;
import lox.token.Tokens.Lexemes;
import lox.util.IterablePeekableIterator;
import lox.util.PeekableIterator;

public class Parser {

    private final Scanner scanner;
    private final CharSequence source;
    private final boolean expressionMode;

    public Parser(final CharSequence source, boolean expressionMode) {
        this.scanner = new Scanner();
        this.source = source;
        this.expressionMode = expressionMode;
    }

    public Result<List<Ast>, List<Throwable>> parse() {
        final var scanResult = scanner.scan(source);

        if (scanResult.hasErr()) {
            return new Result<>(Collections.emptyList(), scanResult.error());
        }

        final var ast = new ArrayList<Ast>();
        final var errors = new ArrayList<Throwable>();

        if (scanResult.isOk()) {
            final var tokens = scanResult.success();

            final var it = new IterablePeekableIterator<Token>(tokens);

            return program(it, errors);
        }

        return new Result<>(ast, errors);
    }

    private Result<List<Ast>, List<Throwable>> program(
            final PeekableIterator<Token> tokens,
            List<Throwable> errors) {
        final var nodes = new ArrayList<Ast>();
        while (tokens.hasNext()) {
            try {
                trace("next: " + tokens.peek().get());
                nodes.add(declaration(tokens));
            } catch (LoxException e) {
                errors.add(e);
            }
        }

        return new Result<>(nodes, errors);
    }

    private Ast declaration(final PeekableIterator<Token> tokens) {
        trace("declaration");
        try {
            final var token = tokens.peek().get();
            return switch (token.lexeme()) {
                case CLASS -> classDecl(tokens);
                case FUN -> funDecl(tokens);
                case VAR -> varDecl(tokens);
                default -> this.expressionMode
                        ? expression(tokens)
                        : statement(tokens);
            };
        } catch (NoSuchElementException e) {
            throw new UnexpectedEofException();
        }
    }

    private Ast classDecl(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("class");
    }

    private Ast funDecl(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("fun");
    }

    // varDecl → "var" IDENTIFIER ( "=" expression )? ";" ;
    private Ast varDecl(final PeekableIterator<Token> tokens) {
        trace("varDecl");
        var varDecl = tokens.next(); // eat 'var' keyword
        assert varDecl.lexeme() == Lexemes.VAR : "expected 'var' keyword";

        final var idToken = tokens.nextIf(
                t -> t.lexeme() == Lexemes.IDENTIFIER);
        if (idToken.isPresent()) {
            final var id = idToken.get();
            Expr initializer = null;
            if (tokens.nextIf(t -> t.lexeme() == Lexemes.EQUAL).isPresent()) {
                initializer = expression(tokens);
            }

            checkSemicolon(tokens);

            trace(
                    "var " + ((IdentifierToken) id).value() + " = " + initializer);
            return new Ast.Var(id, initializer);
        }

        var exception = tokens.peek().isPresent()
                ? new UnexpectedTokenException(tokens.peek().get().toString())
                : new UnexpectedEofException();
        throw exception;
    }

    private Ast statement(final PeekableIterator<Token> tokens) {
        trace("stmt");
        final var ast = switch (tokens.peek().get().lexeme()) {
            case LEFT_BRACE -> block(tokens);
            case WHILE -> whileStmt(tokens);
            case RETURN -> returnStmt(tokens);
            case FOR -> forStmt(tokens);
            case IF -> ifStmt(tokens);
            case PRINT -> printStmt(tokens);
            default -> exprStmt(tokens);
        };

        return ast;
    }

    private Ast exprStmt(final PeekableIterator<Token> tokens) {
        trace("exprStmt");
        if (tokens.hasNext()) {
            var expr = expression(tokens);
            checkSemicolon(tokens);
            return new Stmt.ExprStmt(expr);
        } else {
            throw new UnexpectedEofException();
        }
    }

    private Expr expression(final PeekableIterator<Token> tokens) {
        trace("expression");
        return assignment(tokens);
    }

    private Expr assignment(final PeekableIterator<Token> tokens) {
        trace("assignment");

        // save in case token actually represents an lvalue
        var token = tokens.peek();

        var left = logical_or(tokens);

        if (tokens.nextIf(t -> t.lexeme() == Lexemes.EQUAL).isPresent()) {
            // assignment: lvalue = rvalue
            var rvalue = assignment(tokens);
            return new Expr.Assignment(token.get(), rvalue);
        }

        return left;
    }

    private Expr logical_or(final PeekableIterator<Token> tokens) {
        trace("logical_or");
        var left = logical_and(tokens);
        while (tokens.hasNext()) {
            var node = tokens.nextIf(t -> t.lexeme() == Lexemes.OR);
            if (node.isPresent()) {
                var op = node.get();
                var right = logical_and(tokens);
                left = new Expr.Logical(left, op, right);
            } else {
                break;
            }
        }

        return left;
    }

    private Expr logical_and(final PeekableIterator<Token> tokens) {
        trace("logical_and");
        var left = equality(tokens);
        while (tokens.hasNext()) {
            var node = tokens.nextIf(t -> t.lexeme() == Lexemes.AND);
            if (node.isPresent()) {
                var op = node.get();
                var right = equality(tokens);
                left = new Expr.Logical(left, op, right);
            } else {
                break;
            }
        }

        return left;
    }

    private Expr equality(final PeekableIterator<Token> tokens) {
        trace("equality");
        var ast = comparison(tokens);

        while (tokens.hasNext()) {
            final var node = tokens.nextIf(
                    t -> t.lexeme() == Lexemes.BANG_EQUAL ||
                            t.lexeme() == Lexemes.EQUAL_EQUAL);
            if (node.isPresent()) {
                final var op = node.get();
                final var rhs = comparison(tokens);
                ast = new Expr.Binary(op, ast, rhs);
            } else {
                break;
            }
        }

        return ast;
    }

    private Expr comparison(final PeekableIterator<Token> tokens) {
        trace("comparison");
        var ast = term(tokens);

        while (tokens.hasNext()) {
            final var node = tokens.nextIf(
                    t -> t.lexeme() == Lexemes.GREATER ||
                            t.lexeme() == Lexemes.GREATER_EQUAL ||
                            t.lexeme() == Lexemes.LESS_EQUAL ||
                            t.lexeme() == Lexemes.LESS);
            if (node.isPresent()) {
                final var op = node.get();
                final var rhs = term(tokens);
                ast = new Expr.Binary(op, ast, rhs);
            } else {
                break;
            }
        }
        return ast;
    }

    private Expr term(final PeekableIterator<Token> tokens) {
        trace("term");

        var ast = factor(tokens);

        while (tokens.hasNext()) {
            final var node = tokens.nextIf(
                    t -> t.lexeme() == Lexemes.PLUS || t.lexeme() == Lexemes.MINUS);

            if (node.isPresent()) {
                final var op = node.get();
                final var rhs = factor(tokens);
                ast = new Expr.Binary(op, ast, rhs);
            } else {
                break;
            }
        }

        return ast;
    }

    private Expr factor(final PeekableIterator<Token> tokens) {
        trace("factor");

        var ast = unary(tokens);

        while (tokens.hasNext()) {
            final var node = tokens.nextIf(
                    t -> t.lexeme() == Lexemes.STAR || t.lexeme() == Lexemes.SLASH);

            if (node.isPresent()) {
                final var op = node.get();
                final var rhs = unary(tokens);
                ast = new Expr.Binary(op, ast, rhs);
            } else {
                break;
            }
        }

        return ast;
    }

    private Expr unary(final PeekableIterator<Token> tokens) {
        trace("unary");
        var e = switch (tokens.nextIf(
                t -> t.lexeme() == Lexemes.BANG ||
                        t.lexeme() == Lexemes.MINUS)) {
            case Optional<Token> o when o.isPresent() -> {
                final var op = o.get();
                final Expr expr = unary(tokens);
                yield new Expr.Unary(op, expr);
            }
            default -> {
                var p = primary(tokens);
                yield p;
            }
        };

        return e;
    }

    private Expr primary(final PeekableIterator<Token> tokens) {
        trace("primary");
        var token = tokens.nextIf(
                t -> t.lexeme() == Lexemes.TRUE ||
                        t.lexeme() == Lexemes.FALSE ||
                        t.lexeme() == Lexemes.NIL);

        if (token.isPresent()) {
            return new Expr.Terminal(token.get());
        }

        token = tokens.nextIf(t -> t.lexeme() == Lexemes.LEFT_PAREN);
        if (token.isPresent()) {
            // start of group expression i.e. '(' expr ')'
            trace("start group expr");
            var expr = expression(tokens);
            token = tokens.nextIf(t -> t.lexeme() == Lexemes.RIGHT_PAREN);
            if (token.isPresent()) {
                trace("end group expr");
                return new Expr.Group(expr);
            } else if (tokens.hasNext()) {
                throw new MissingTokenException(
                        ")",
                        tokens.peek().get().lexeme().value());
            } else {
                throw new UnexpectedEofException();
            }
        }

        token = tokens.nextIf(
                t -> t.lexeme() == Lexemes.NUMBER ||
                        t.lexeme() == Lexemes.STRING ||
                        t.lexeme() == Lexemes.IDENTIFIER);
        if (token.isPresent()) {
            return new Expr.Terminal(token.get());
        } else if (tokens.hasNext()) {
            throw new UnexpectedTokenException(tokens.next().lexeme().value());
        } else {
            throw new UnexpectedEofException();
        }
    }

    private Ast block(final PeekableIterator<Token> tokens) {
        trace("block start");
        var _leftBrace = tokens.next();
        assert _leftBrace.lexeme() == Lexemes.LEFT_BRACE;

        final List<Ast> blockStatements = new ArrayList<>();
        while (tokens.hasNext()) {
            var ast = declaration(tokens);
            blockStatements.add(ast);

            if (!tokens.hasNext()) {
                throw new UnexpectedEofException();
            }

            if (tokens
                    .nextIf(t -> t.lexeme() == Lexemes.RIGHT_BRACE)
                    .isPresent()) {
                trace("block end");
                break;
            }
        }

        return new Ast.Block(blockStatements);
    }

    private Ast printStmt(final PeekableIterator<Token> tokens) {
        trace("printStmt");

        var _printToken = tokens.next();
        assert _printToken.lexeme() == Lexemes.PRINT;

        if (tokens.hasNext()) {
            var expr = expression(tokens);
            checkSemicolon(tokens);
            return new Stmt.PrintStmt(expr);
        } else {
            throw new UnexpectedEofException();
        }
    }

    private Ast ifStmt(final PeekableIterator<Token> tokens) {
        trace("ifStmt");
        var _ifToken = tokens.next();
        assert _ifToken.lexeme() == Lexemes.IF;

        if (tokens.hasNext()) {
            var cond = expression(tokens);
            var thenStmt = statement(tokens);
            Ast elseStmt = null;
            var token = tokens.nextIf(t -> t.lexeme() == Lexemes.ELSE);
            if (token.isPresent()) {
                elseStmt = statement(tokens);
            }

            return new Stmt.IfStmt(cond, thenStmt, elseStmt);
        } else {
            throw new UnexpectedEofException();
        }
    }

    private Ast whileStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("while statement");
    }

    private Ast returnStmt(final PeekableIterator<Token> tokens) {
        checkSemicolon(tokens);
        throw new NotImplementedException("return statement");
    }

    private Ast forStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("for statement");
    }

    // check for and eat semicolon, or else error
    private void checkSemicolon(final PeekableIterator<Token> tokens) {
        trace("check semicolon");
        tokens
                .nextIf(t -> t.lexeme() == Lexemes.SEMICOLON)
                .orElseThrow(() -> {
                    if (tokens.hasNext()) {
                        return new MissingTokenException(
                                Lexemes.SEMICOLON.value(),
                                tokens.peek().get().lexeme().value());
                    } else {
                        return new UnexpectedEofException();
                    }
                });
    }
}
