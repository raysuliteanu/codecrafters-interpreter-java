package lox.parse;

import static lox.util.LogUtil.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lox.LoxException;
import lox.NotImplementedException;
import lox.Result;
import lox.token.Scanner;
import lox.token.Token;
import lox.token.Tokens.Lexemes;
import lox.util.IterablePeekableIterator;
import lox.util.PeekableIterator;
import lox.util.UnexpectedEofException;

public class Parser {

    private final Scanner scanner;
    private final CharSequence source;

    public Parser(final CharSequence source) {
        this.scanner = new Scanner();
        this.source = source;
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

    private Result<List<Ast>, List<Throwable>> program(final PeekableIterator<Token> tokens, List<Throwable> errors) {
        final var nodes = new ArrayList<Ast>();
        while (tokens.hasNext()) {
            try {
                final var token = tokens.peek().get();
                trace("next token: " + token);
                final var ast = switch (token.lexeme()) {
                    case CLASS -> throw new NotImplementedException(token.lexeme().name());
                    case FUN -> throw new NotImplementedException(token.lexeme().name());
                    case VAR -> throw new NotImplementedException(token.lexeme().name());
                    default -> statement(tokens);
                };
                nodes.add(ast);
            } catch (LoxException e) {
                errors.add(e);
            }
        }

        return new Result<>(nodes, errors);
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
            default -> expressionStatement(tokens);
        };

        return ast;
    }

    private Ast expressionStatement(final PeekableIterator<Token> tokens) {
        trace("expression stmt");
        final var ast = expression(tokens);

        // check for an eat semicolon, or else error
        // TODO: for now, comment out checking for semicolon
        // since we're not at the statement stage in CC
        /*
         * tokens.nextIf((t) -> t.lexeme() == Lexemes.SEMICOLON)
         * .orElseThrow(() -> {
         * if (tokens.hasNext()) {
         * return new MissingTokenException(
         * Lexemes.SEMICOLON.value(),
         * tokens.peek().get().lexeme().value());
         * } else {
         * return new UnexpectedEofException();
         * }
         * });
         */

        return ast;
    }

    private Expr expression(final PeekableIterator<Token> tokens) {
        trace("expression");
        return equality(tokens);
    }

    private Expr equality(final PeekableIterator<Token> tokens) {
        trace("equality");
        var ast = comparison(tokens);

        while (tokens.hasNext()) {
            final var node = tokens
                    .nextIf((t) -> t.lexeme() == Lexemes.BANG_EQUAL || t.lexeme() == Lexemes.EQUAL_EQUAL);
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
            final var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.GREATER ||
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
            final var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.PLUS || t.lexeme() == Lexemes.MINUS);

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
            final var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.STAR || t.lexeme() == Lexemes.SLASH);

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
        var e = switch (tokens.nextIf((t) -> t.lexeme() == Lexemes.BANG || t.lexeme() == Lexemes.MINUS)) {
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
        var token = tokens
                .nextIf((t) -> t.lexeme() == Lexemes.TRUE || t.lexeme() == Lexemes.FALSE || t.lexeme() == Lexemes.NIL);

        if (token.isPresent()) {
            return new Expr.Terminal(token.get());
        }

        token = tokens.nextIf((t) -> t.lexeme() == Lexemes.LEFT_PAREN);
        if (token.isPresent()) {
            // start of group expression i.e. '(' expr ')'
            trace("start group expr");
            var expr = expression(tokens);
            token = tokens.nextIf((t) -> t.lexeme() == Lexemes.RIGHT_PAREN);
            if (token.isPresent()) {
                trace("end group expr");
                return new Expr.Group(expr);
            } else if (tokens.hasNext()) {
                throw new MissingTokenException(")", tokens.peek().get().lexeme().value());
            } else {
                throw new UnexpectedEofException();
            }
        }

        token = tokens.nextIf(
                (t) -> t.lexeme() == Lexemes.NUMBER || t.lexeme() == Lexemes.STRING
                        || t.lexeme() == Lexemes.IDENTIFIER);
        if (token.isPresent()) {
            return new Expr.Terminal(token.get());
        } else if (tokens.hasNext()) {
            throw new UnexpectedTokenException(tokens.next().lexeme().value());
        } else {
            throw new UnexpectedEofException();
        }
    }

    private Ast block(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("block statement");
    }

    private Ast whileStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("while statement");
    }

    private Ast returnStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("return statement");
    }

    private Ast forStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("for statement");
    }

    private Ast ifStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("if statement");
    }

    private Ast printStmt(final PeekableIterator<Token> tokens) {
        throw new NotImplementedException("print statement");
    }

}
