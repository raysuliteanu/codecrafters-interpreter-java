package lox.parse;

import java.util.Objects;
import lox.token.IdentifierToken;
import lox.token.Token;

public abstract sealed class Expr extends Ast {

    public static final class Logical extends Expr {

        private final Expr left;
        private final Token op;
        private final Expr right;

        public Logical(Expr left, Token op, Expr right) {
            Objects.nonNull(left);
            Objects.nonNull(right);
            Objects.nonNull(op);
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public Expr left() {
            return left;
        }

        public Expr right() {
            return right;
        }

        public Token op() {
            return op;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitLogical(this);
        }
    }

    public static final class Terminal extends Expr {

        private final Token token;

        public Terminal(Token token) {
            Objects.nonNull(token);
            this.token = token;
        }

        public Token token() {
            return token;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitTerminal(this);
        }
    }

    public static final class Group extends Expr {

        private final Expr ast;

        public Group(Expr ast) {
            Objects.nonNull(ast);
            this.ast = ast;
        }

        public Expr group() {
            return ast;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitGroup(this);
        }
    }

    public static final class Unary extends Expr {

        private final Token token;
        private final Expr expr;

        public Unary(Token token, Expr ast) {
            Objects.nonNull(token);
            Objects.nonNull(ast);
            this.token = token;
            this.expr = ast;
        }

        public Token token() {
            return token;
        }

        public Expr expr() {
            return expr;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitUnary(this);
        }
    }

    public static final class Binary extends Expr {

        private final Expr left;
        private final Expr right;
        private final Token op;

        public Binary(Token op, Expr left, Expr right) {
            Objects.nonNull(left);
            Objects.nonNull(right);
            Objects.nonNull(op);
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public Expr left() {
            return left;
        }

        public Expr right() {
            return right;
        }

        public Token op() {
            return op;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitBinary(this);
        }
    }

    public static final class Assignment extends Expr {

        private final Token id;
        private final Expr expr;

        public Assignment(final Token id, final Expr expr) {
            this.id = id;
            this.expr = expr;
        }

        public Token identifier() {
            return id;
        }

        public Expr expression() {
            return expr;
        }

        @Override
        public <T> T accept(AstVisitor<T> visitor) {
            return visitor.visitAssignment(this);
        }
    }
}
