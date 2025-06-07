package lox.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lox.NotImplementedException;
import lox.Result;
import lox.token.Scanner;
import lox.token.Token;
import lox.token.Tokens.Lexemes;
import lox.util.IterablePeekableIterator;
import lox.util.PeekableIterator;

public class Parser {

  private final Scanner scanner;
  private final CharSequence source;

  public Parser(final CharSequence source) {
    this.scanner = new Scanner();
    this.source = source;
  }

  public Result<List<Ast>, List<Throwable>> parse() {
    var scanResult = scanner.scan(source);

    // TODO: handle possible errors from scan()

    var ast = new ArrayList<Ast>();
    var errors = new ArrayList<Throwable>();

    if (scanResult.isOk()) {
      var tokens = scanResult.success();

      var it = new IterablePeekableIterator<Token>(tokens);

      program(it);
    }

    return new Result<>(ast, errors);
  }

  private List<Ast> program(PeekableIterator<Token> tokens) {
    var nodes = new ArrayList<Ast>();
    while (tokens.hasNext()) {
      var token = tokens.peek().get();
      var ast = switch (token.lexeme()) {
        case CLASS -> throw new NotImplementedException(token.lexeme().name());
        case FUN -> throw new NotImplementedException(token.lexeme().name());
        case VAR -> throw new NotImplementedException(token.lexeme().name());
        default -> statement(tokens);
      };
      nodes.add(ast);
    }

    return nodes;
  }

  private Ast statement(PeekableIterator<Token> tokens) {
    var ast = switch (tokens.peek().get().lexeme()) {
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

  private Ast expressionStatement(PeekableIterator<Token> tokens) {
    var ast = expression(tokens);

    // check for an eat semicolon, or else error
    tokens.nextIf((t) -> t.lexeme() == Lexemes.SEMICOLON)
        .orElseThrow(() -> new MissingTokenException(
            Lexemes.SEMICOLON.lexeme(),
            tokens.peek().get().lexeme().lexeme()));

    return ast;
  }

  private Expr expression(PeekableIterator<Token> tokens) {
    return equality(tokens);
  }

  private Expr equality(PeekableIterator<Token> tokens) {
    var ast = comparison(tokens);

    while (tokens.peek().isPresent()) {
      var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.BANG_EQUAL || t.lexeme() == Lexemes.EQUAL_EQUAL);
      if (node.isPresent()) {
        var op = node.get();
        var rhs = comparison(tokens);
        ast = new Expr.Binary(op, ast, rhs);
      }
    }

    return ast;
  }

  private Expr comparison(PeekableIterator<Token> tokens) {
    var ast = term(tokens);

    while (tokens.peek().isPresent()) {
      var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.GREATER ||
          t.lexeme() == Lexemes.GREATER_EQUAL ||
          t.lexeme() == Lexemes.LESS_EQUAL ||
          t.lexeme() == Lexemes.LESS);
      if (node.isPresent()) {
        var op = node.get();
        var rhs = term(tokens);
        ast = new Expr.Binary(op, ast, rhs);
      }
    }
    return ast;
  }

  private Expr term(PeekableIterator<Token> tokens) {
    var ast = factor(tokens);

    while (tokens.peek().isPresent()) {
      var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.PLUS || t.lexeme() == Lexemes.MINUS);

      if (node.isPresent()) {
        var op = node.get();
        var rhs = factor(tokens);
        ast = new Expr.Binary(op, ast, rhs);
      }
    }

    return ast;
  }

  private Expr factor(PeekableIterator<Token> tokens) {
    var ast = unary(tokens);

    while (tokens.peek().isPresent()) {
      var node = tokens.nextIf((t) -> t.lexeme() == Lexemes.STAR || t.lexeme() == Lexemes.SLASH);

      if (node.isPresent()) {
        var op = node.get();
        var rhs = unary(tokens);
        ast = new Expr.Binary(op, ast, rhs);
      }
    }

    return ast;
  }

  private Expr unary(PeekableIterator<Token> tokens) {
    Expr ast = null;
    var token = tokens.peek().get();
    if (token.lexeme() == Lexemes.BANG || token.lexeme() == Lexemes.MINUS) {
      token = tokens.next();
      Expr expr = primary(tokens);
      ast = new Expr.Unary(token, expr);
    } else {
      ast = primary(tokens);
    }
    return ast;
  }

  private Expr primary(PeekableIterator<Token> tokens) {
    return new Expr.Terminal(null);
  }

  private Ast block(PeekableIterator<Token> tokens) {
    throw new NotImplementedException("block statement");
  }

  private Ast whileStmt(PeekableIterator<Token> tokens) {
    throw new NotImplementedException("while statement");
  }

  private Ast returnStmt(PeekableIterator<Token> tokens) {
    throw new NotImplementedException("return statement");
  }

  private Ast forStmt(PeekableIterator<Token> tokens) {
    throw new NotImplementedException("for statement");
  }

  private Ast ifStmt(PeekableIterator<Token> tokens) {
    throw new NotImplementedException("if statement");
  }

  private Ast printStmt(PeekableIterator<Token> tokens) {
    throw new NotImplementedException("print statement");
  }

}
