package lox.parse;

import java.util.Optional;
import lox.token.Token;

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

    public static final class Var extends Ast {
        private final Token identifier;
        private final Optional<Expr> initializer;

        public Var(Token identifier) {
            this(identifier, null);
        }

        public Var(Token identifier, Expr initializer) {
            this.identifier = identifier;
            this.initializer = Optional.ofNullable(initializer);
        }

        public Token identifier() {
            return identifier;
        }

        public Optional<Expr> initializer() {
            return initializer;
        }

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
