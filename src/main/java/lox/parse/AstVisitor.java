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

public interface AstVisitor<T> {
    T visitTerminal(Expr.Terminal expr);

    T visitGroup(Expr.Group expr);

    T visitUnary(Expr.Unary expr);

    T visitBinary(Expr.Binary expr);

    T visitAssignment(Expr.Assignment expr);

    T visitExprStmt(ExprStmt exprStmt);

    T visitPrintStmt(PrintStmt printStmt);

    T visitReturnStmt(ReturnStmt returnStmt);

    T visitWhileStmt(WhileStmt whileStmt);

    T visitForStmt(ForStmt forStmt);

    T visitIfStmt(IfStmt ifStmt);

    T visitClazz(Clazz clazz);

    T visitFunc(Func func);

    T visitVar(Var var);

    T visitBlock(Block block);
}
