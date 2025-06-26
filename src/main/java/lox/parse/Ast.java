package lox.parse;

import java.util.Optional;
import java.util.List;
import lox.token.Token;

public abstract sealed class Ast permits
        Ast.Clazz, Ast.Func, Ast.Var, Ast.Block, Expr, Stmt {

    private static final AstPrinter PRINTER = new AstPrinter();

    public abstract <T> T accept(AstVisitor<T> visitor);

    @Override
    public String toString() {
        return PRINTER.print(this);
    }

    public static final class Clazz extends Ast {
        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitClazz(this);
        }
    }

    public static final class Func extends Ast {
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

    public static final class Block extends Ast {
        private final List<Ast> block;

        public Block(final List<Ast> block) {
            this.block = block;
        }

        public List<Ast> block() {
            return block;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitBlock(this);
        }
    }
}
