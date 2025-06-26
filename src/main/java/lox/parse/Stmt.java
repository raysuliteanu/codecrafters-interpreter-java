package lox.parse;

import java.util.Optional;

public abstract sealed class Stmt extends Ast {
    public static final class IfStmt extends Stmt {
        private final Expr condition;
        private final Ast thenStmt;
        private final Optional<Ast> elseStmt;

        public IfStmt(final Expr condition, final Stmt thenStmt) {
            this(condition, thenStmt, null);
        }

        public IfStmt(final Expr condition, final Ast thenStmt, final Ast elseStmt) {
            this.condition = condition;
            this.thenStmt = thenStmt;
            this.elseStmt = Optional.ofNullable(elseStmt);
        }

        public Expr condition() {
            return condition;
        }

        public Ast thenStmt() {
            return thenStmt;
        }

        public Optional<Ast> elseStmt() {
            return elseStmt;
        }

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
        private final Expr expr;

        public PrintStmt(Expr expr) {
            this.expr = expr;
        }

        public Expr expr() {
            return expr;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    public static final class ExprStmt extends Stmt {
        private final Expr expr;

        public ExprStmt(Expr expr) {
            this.expr = expr;
        }

        public Expr expr() {
            return expr;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitExprStmt(this);
        }
    }
}
