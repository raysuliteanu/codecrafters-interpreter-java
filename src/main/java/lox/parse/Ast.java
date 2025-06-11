package lox.parse;

public abstract sealed class Ast permits
    Ast.Clazz, Ast.Func, Ast.Var, Ast.Block, Expr, Stmt {

  private static final AstPrinter PRINTER = new AstPrinter();

  public abstract <T> T accept(AstVisitor<T> visitor);

  @Override
  public String toString() {
    return PRINTER.print(this);
  }

  public final class Clazz extends Ast {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitClazz(this);
    }
  }

  public final class Func extends Ast {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitFunc(this);
    }
  }

  public final class Var extends Ast {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitVar(this);
    }
  }

  public final class Block extends Ast {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
      return visitor.visitBlock(this);
    }
  }
}
