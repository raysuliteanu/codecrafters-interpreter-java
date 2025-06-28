package lox.parse;

import lox.parse.Ast.Block;
import lox.parse.Ast.Clazz;
import lox.parse.Ast.Func;
import lox.parse.Ast.Var;
import lox.parse.Stmt.ExprStmt;
import lox.parse.Stmt.ForStmt;
import lox.parse.Stmt.IfStmt;
import lox.parse.Stmt.PrintStmt;
import lox.parse.Stmt.ReturnStmt;
import lox.parse.Stmt.WhileStmt;
import lox.token.IdentifierToken;

public class AstPrinter implements AstVisitor<String> {

    public String print(Ast ast) {
        return ast.accept(this);
    }

    @Override
    public String visitLogical(Expr.Logical expr) {
        return expr.left() + " " + expr.op().lexeme().value() + " " + expr.right();
    }

    @Override
    public String visitTerminal(Expr.Terminal expr) {
        if (expr.token() instanceof lox.token.ValueToken vt) {
            return vt.value().toString();
        } else {
            return expr.token().lexeme().value();
        }
    }

    @Override
    public String visitGroup(Expr.Group expr) {
        return "(group " + expr.group() + ")";
    }

    @Override
    public String visitUnary(Expr.Unary expr) {
        return "(" + expr.token().lexeme().value() + " " + expr.expr() + ")";
    }

    @Override
    public String visitBinary(Expr.Binary expr) {
        return ("(" +
                expr.op().lexeme().value() +
                " " +
                expr.left() +
                " " +
                expr.right() +
                ")");
    }

    @Override
    public String visitAssignment(Expr.Assignment expr) {
        return (((IdentifierToken) expr.identifier()).value() +
                " = " +
                expr.expression());
    }

    @Override
    public String visitIfStmt(IfStmt ifStmt) {
        final StringBuilder sb = new StringBuilder("if (");
        sb.append(ifStmt.condition()).append(")\n").append(ifStmt.thenStmt());

        if (ifStmt.elseStmt().isPresent()) {
            sb.append("else").append(ifStmt.elseStmt());
        }

        return sb.toString();
    }

    @Override
    public String visitWhileStmt(WhileStmt whileStmt) {
        final StringBuilder sb = new StringBuilder("while (");
        sb.append(whileStmt.condition()).append(")\n")
                .append(whileStmt.body());

        return sb.toString();
    }

    @Override
    public String visitVar(Var var) {
        var s = ((IdentifierToken) var.identifier()).value();
        if (var.initializer().isPresent()) {
            s += " = " + var.initializer();
        }
        return s;
    }

    @Override
    public String visitBlock(Block block) {
        final StringBuilder sb = new StringBuilder("{\n");
        for (var ast : block.block()) {
            sb.append(ast).append("\n");
        }
        return sb.append("}").toString();
    }

    @Override
    public String visitPrintStmt(PrintStmt printStmt) {
        return "print " + printStmt.expr();
    }

    @Override
    public String visitExprStmt(ExprStmt exprStmt) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'visitExprStmt'");
    }

    @Override
    public String visitReturnStmt(ReturnStmt returnStmt) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'visitReturnStmt'");
    }

    @Override
    public String visitForStmt(ForStmt forStmt) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'visitForStmt'");
    }

    @Override
    public String visitClazz(Clazz clazz) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'visitClazz'");
    }

    @Override
    public String visitFunc(Func func) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'visitFunc'");
    }
}
