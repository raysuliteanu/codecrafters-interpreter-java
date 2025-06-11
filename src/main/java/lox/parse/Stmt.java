package lox.parse;

public abstract sealed class Stmt extends Ast {
  public static final class IfStmt extends Stmt {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitIfStmt(this);
    }
  }

  public static final class ForStmt extends Stmt {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitForStmt(this);
    }
  }

  public static final class WhileStmt extends Stmt {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitWhileStmt(this);
    }
  }

  public static final class ReturnStmt extends Stmt {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitReturnStmt(this);
    }
  }

  public static final class PrintStmt extends Stmt {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitPrintStmt(this);
    }
  }

  public static final class ExprStmt extends Stmt {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitExprStmt(this);
    }
  }
}
