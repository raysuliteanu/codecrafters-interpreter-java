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

public class AstPrinter implements AstVisitor<String> {

    public String print(Ast ast) {
        return ast.accept(this);
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
        return "(" + expr.op().lexeme().value() + " " + expr.left() + " " + expr.right() + ")";
    }

    @Override
    public String visitExprStmt(ExprStmt exprStmt) {
        throw new UnsupportedOperationException("Unimplemented method 'visitExprStmt'");
    }

    @Override
    public String visitPrintStmt(PrintStmt printStmt) {
        throw new UnsupportedOperationException("Unimplemented method 'visitPrintStmt'");
    }

    @Override
    public String visitReturnStmt(ReturnStmt returnStmt) {
        throw new UnsupportedOperationException("Unimplemented method 'visitReturnStmt'");
    }

    @Override
    public String visitWhileStmt(WhileStmt whileStmt) {
        throw new UnsupportedOperationException("Unimplemented method 'visitWhileStmt'");
    }

    @Override
    public String visitForStmt(ForStmt forStmt) {
        throw new UnsupportedOperationException("Unimplemented method 'visitForStmt'");
    }

    @Override
    public String visitIfStmt(IfStmt ifStmt) {
        throw new UnsupportedOperationException("Unimplemented method 'visitIfStmt'");
    }

    @Override
    public String visitClazz(Clazz clazz) {
        throw new UnsupportedOperationException("Unimplemented method 'visitClazz'");
    }

    @Override
    public String visitFunc(Func func) {
        throw new UnsupportedOperationException("Unimplemented method 'visitFunc'");
    }

    @Override
    public String visitVar(Var var) {
        throw new UnsupportedOperationException("Unimplemented method 'visitVar'");
    }

    @Override
    public String visitBlock(Block block) {
        throw new UnsupportedOperationException("Unimplemented method 'visitBlock'");
    }
}
