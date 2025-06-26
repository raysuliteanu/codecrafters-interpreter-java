package lox.parse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import lox.token.Tokens.Lexemes;
import lox.parse.Ast.Var;
import lox.parse.Ast.Block;
import lox.parse.Expr.Terminal;
import lox.parse.Expr.Binary;
import lox.parse.Expr.Unary;
import lox.parse.Expr.Group;
import lox.parse.Expr.Assignment;
import lox.parse.Stmt.PrintStmt;
import lox.parse.Stmt.ExprStmt;
import lox.NotImplementedException;

class ParserTest {

    @Test
    void shouldParseSimpleNumber() {
        Parser parser = new Parser("42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(ExprStmt.class);

        ExprStmt exprStmt = (ExprStmt) ast;
        assertThat(exprStmt.expr()).isInstanceOf(Terminal.class);

        Terminal terminal = (Terminal) exprStmt.expr();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseSimpleString() {
        Parser parser = new Parser("\"hello\";", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(ExprStmt.class);

        ExprStmt exprStmt = (ExprStmt) ast;
        assertThat(exprStmt.expr()).isInstanceOf(Terminal.class);

        Terminal terminal = (Terminal) exprStmt.expr();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.STRING);
    }

    @Test
    void shouldParseSimpleIdentifier() {
        Parser parser = new Parser("myVar;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(ExprStmt.class);

        ExprStmt exprStmt = (ExprStmt) ast;
        assertThat(exprStmt.expr()).isInstanceOf(Terminal.class);

        Terminal terminal = (Terminal) exprStmt.expr();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
    }

    @Test
    void shouldParseBooleanLiterals() {
        Parser parser1 = new Parser("true;", false);
        Parser parser2 = new Parser("false;", false);
        Parser parser3 = new Parser("nil;", false);

        var result1 = parser1.parse();
        var result2 = parser2.parse();
        var result3 = parser3.parse();

        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();

        Terminal terminal1 = (Terminal) ((ExprStmt) result1.success().get(0)).expr();
        Terminal terminal2 = (Terminal) ((ExprStmt) result2.success().get(0)).expr();
        Terminal terminal3 = (Terminal) ((ExprStmt) result3.success().get(0)).expr();

        assertThat(terminal1.token().lexeme()).isEqualTo(Lexemes.TRUE);
        assertThat(terminal2.token().lexeme()).isEqualTo(Lexemes.FALSE);
        assertThat(terminal3.token().lexeme()).isEqualTo(Lexemes.NIL);
    }

    @Test
    void shouldParseGroupedExpression() {
        Parser parser = new Parser("(42);", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Group.class);

        Group group = (Group) exprStmt.expr();
        assertThat(group.group()).isInstanceOf(Terminal.class);

        Terminal terminal = (Terminal) group.group();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseUnaryExpression() {
        Parser parser1 = new Parser("-42;", false);
        Parser parser2 = new Parser("!true;", false);

        var result1 = parser1.parse();
        var result2 = parser2.parse();

        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();

        ExprStmt exprStmt1 = (ExprStmt) result1.success().get(0);
        ExprStmt exprStmt2 = (ExprStmt) result2.success().get(0);

        assertThat(exprStmt1.expr()).isInstanceOf(Unary.class);
        assertThat(exprStmt2.expr()).isInstanceOf(Unary.class);

        Unary unary1 = (Unary) exprStmt1.expr();
        Unary unary2 = (Unary) exprStmt2.expr();

        assertThat(unary1.token().lexeme()).isEqualTo(Lexemes.MINUS);
        assertThat(unary2.token().lexeme()).isEqualTo(Lexemes.BANG);

        assertThat(unary1.expr()).isInstanceOf(Terminal.class);
        assertThat(unary2.expr()).isInstanceOf(Terminal.class);
    }

    @Test
    void shouldParseBinaryArithmeticExpressions() {
        Parser parser1 = new Parser("1 + 2;", false);
        Parser parser2 = new Parser("3 - 4;", false);
        Parser parser3 = new Parser("5 * 6;", false);
        Parser parser4 = new Parser("7 / 8;", false);

        var result1 = parser1.parse();
        var result2 = parser2.parse();
        var result3 = parser3.parse();
        var result4 = parser4.parse();

        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        assertThat(result4.isOk()).isTrue();

        Binary binary1 = (Binary) ((ExprStmt) result1.success().get(0)).expr();
        Binary binary2 = (Binary) ((ExprStmt) result2.success().get(0)).expr();
        Binary binary3 = (Binary) ((ExprStmt) result3.success().get(0)).expr();
        Binary binary4 = (Binary) ((ExprStmt) result4.success().get(0)).expr();

        assertThat(binary1.op().lexeme()).isEqualTo(Lexemes.PLUS);
        assertThat(binary2.op().lexeme()).isEqualTo(Lexemes.MINUS);
        assertThat(binary3.op().lexeme()).isEqualTo(Lexemes.STAR);
        assertThat(binary4.op().lexeme()).isEqualTo(Lexemes.SLASH);

        assertThat(binary1.left()).isInstanceOf(Terminal.class);
        assertThat(binary1.right()).isInstanceOf(Terminal.class);
    }

    @Test
    void shouldParseBinaryComparisonExpressions() {
        Parser parser1 = new Parser("1 < 2;", false);
        Parser parser2 = new Parser("3 <= 4;", false);
        Parser parser3 = new Parser("5 > 6;", false);
        Parser parser4 = new Parser("7 >= 8;", false);

        var result1 = parser1.parse();
        var result2 = parser2.parse();
        var result3 = parser3.parse();
        var result4 = parser4.parse();

        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        assertThat(result4.isOk()).isTrue();

        Binary binary1 = (Binary) ((ExprStmt) result1.success().get(0)).expr();
        Binary binary2 = (Binary) ((ExprStmt) result2.success().get(0)).expr();
        Binary binary3 = (Binary) ((ExprStmt) result3.success().get(0)).expr();
        Binary binary4 = (Binary) ((ExprStmt) result4.success().get(0)).expr();

        assertThat(binary1.op().lexeme()).isEqualTo(Lexemes.LESS);
        assertThat(binary2.op().lexeme()).isEqualTo(Lexemes.LESS_EQUAL);
        assertThat(binary3.op().lexeme()).isEqualTo(Lexemes.GREATER);
        assertThat(binary4.op().lexeme()).isEqualTo(Lexemes.GREATER_EQUAL);
    }

    @Test
    void shouldParseBinaryEqualityExpressions() {
        Parser parser1 = new Parser("1 == 2;", false);
        Parser parser2 = new Parser("3 != 4;", false);

        var result1 = parser1.parse();
        var result2 = parser2.parse();

        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();

        Binary binary1 = (Binary) ((ExprStmt) result1.success().get(0)).expr();
        Binary binary2 = (Binary) ((ExprStmt) result2.success().get(0)).expr();

        assertThat(binary1.op().lexeme()).isEqualTo(Lexemes.EQUAL_EQUAL);
        assertThat(binary2.op().lexeme()).isEqualTo(Lexemes.BANG_EQUAL);
    }

    @Test
    void shouldParseComplexExpressionWithPrecedence() {
        Parser parser = new Parser("1 + 2 * 3;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();

        // Should parse as 1 + (2 * 3) due to precedence
        Binary outerBinary = (Binary) ((ExprStmt) result.success().get(0)).expr();
        assertThat(outerBinary.op().lexeme()).isEqualTo(Lexemes.PLUS);
        assertThat(outerBinary.left()).isInstanceOf(Terminal.class);
        assertThat(outerBinary.right()).isInstanceOf(Binary.class);

        Binary innerBinary = (Binary) outerBinary.right();
        assertThat(innerBinary.op().lexeme()).isEqualTo(Lexemes.STAR);
    }

    @Test
    void shouldParseAssignmentExpression() {
        Parser parser = new Parser("x = 42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Assignment assignment = (Assignment) ((ExprStmt) result.success().get(0)).expr();
        assertThat(assignment.identifier().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
        assertThat(assignment.expression()).isInstanceOf(Terminal.class);

        Terminal terminal = (Terminal) assignment.expression();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseVariableDeclarationWithoutInitializer() {
        Parser parser = new Parser("var x;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(Var.class);

        Var varDecl = (Var) ast;
        assertThat(varDecl.identifier().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
        assertThat(varDecl.initializer()).isEmpty();
    }

    @Test
    void shouldParseVariableDeclarationWithInitializer() {
        Parser parser = new Parser("var x = 42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(Var.class);

        Var varDecl = (Var) ast;
        assertThat(varDecl.identifier().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
        assertThat(varDecl.initializer()).isPresent();
        assertThat(varDecl.initializer().get()).isInstanceOf(Terminal.class);
    }

    @Test
    void shouldParsePrintStatement() {
        Parser parser = new Parser("print 42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(PrintStmt.class);

        PrintStmt printStmt = (PrintStmt) ast;
        assertThat(printStmt.expr()).isInstanceOf(Terminal.class);

        Terminal terminal = (Terminal) printStmt.expr();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseBlockStatement() {
        Parser parser = new Parser("{ var x = 1; print x; }", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(Block.class);

        Block block = (Block) ast;
        assertThat(block.block()).hasSize(2);
        assertThat(block.block().get(0)).isInstanceOf(Var.class);
        assertThat(block.block().get(1)).isInstanceOf(PrintStmt.class);
    }

    @Test
    void shouldParseEmptyBlock() {
        Parser parser = new Parser("{ }", false);

        var result = parser.parse();

        // Empty block should fail because Parser expects at least one statement in a
        // block
        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
    }

    @Test
    void shouldParseMultipleStatements() {
        Parser parser = new Parser("var x = 1; print x; var y = 2;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(3);

        assertThat(result.success().get(0)).isInstanceOf(Var.class);
        assertThat(result.success().get(1)).isInstanceOf(PrintStmt.class);
        assertThat(result.success().get(2)).isInstanceOf(Var.class);
    }

    @Test
    void shouldHandleExpressionMode() {
        Parser parser = new Parser("1 + 2", true);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(Binary.class);

        Binary binary = (Binary) ast;
        assertThat(binary.op().lexeme()).isEqualTo(Lexemes.PLUS);
    }

    @Test
    void shouldHandleScannerErrors() {
        Parser parser = new Parser("\"unterminated string", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
    }

    @Test
    void shouldHandleUnexpectedToken() {
        Parser parser = new Parser("42 42;", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(ParseException.class);
    }

    @Test
    void shouldHandleMissingSemicolon() {
        Parser parser = new Parser("42", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(UnexpectedEofException.class);
    }

    @Test
    void shouldHandleUnterminatedGroup() {
        Parser parser = new Parser("(42", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(UnexpectedEofException.class);
    }

    @Test
    void shouldHandleUnterminatedBlock() {
        Parser parser = new Parser("{ var x = 1;", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(UnexpectedEofException.class);
    }

    @Test
    void shouldHandleInvalidVariableDeclaration() {
        Parser parser = new Parser("var 42;", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(UnexpectedTokenException.class);
    }

    @Test
    void shouldHandleEmptyInput() {
        Parser parser = new Parser("", false);

        var result = parser.parse();

        // Empty input should still be successful but with empty result list
        assertThat(result.success()).isEmpty();
    }

    @Test
    void shouldHandleWhitespaceOnly() {
        Parser parser = new Parser("   \n\t  ", false);

        var result = parser.parse();

        // Whitespace-only input should be successful but with empty result list
        assertThat(result.success()).isEmpty();
    }

    @Test
    void shouldHandleComments() {
        Parser parser = new Parser("// this is a comment\n42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        Terminal terminal = (Terminal) exprStmt.expr();
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseNestedGroups() {
        Parser parser = new Parser("((42));", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        Group outerGroup = (Group) exprStmt.expr();
        Group innerGroup = (Group) outerGroup.group();
        Terminal terminal = (Terminal) innerGroup.group();

        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseChainedUnaryOperators() {
        Parser parser = new Parser("--42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        Unary outerUnary = (Unary) exprStmt.expr();
        Unary innerUnary = (Unary) outerUnary.expr();
        Terminal terminal = (Terminal) innerUnary.expr();

        assertThat(outerUnary.token().lexeme()).isEqualTo(Lexemes.MINUS);
        assertThat(innerUnary.token().lexeme()).isEqualTo(Lexemes.MINUS);
        assertThat(terminal.token().lexeme()).isEqualTo(Lexemes.NUMBER);
    }

    @Test
    void shouldParseChainedAssignments() {
        Parser parser = new Parser("x = y = 42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        Assignment outerAssignment = (Assignment) exprStmt.expr();
        Assignment innerAssignment = (Assignment) outerAssignment.expression();

        assertThat(outerAssignment.identifier().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
        assertThat(innerAssignment.identifier().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
        assertThat(innerAssignment.expression()).isInstanceOf(Terminal.class);
    }
}
