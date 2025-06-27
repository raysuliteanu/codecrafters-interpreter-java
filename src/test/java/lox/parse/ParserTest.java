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
import lox.parse.Expr.Logical;
import lox.parse.Stmt.PrintStmt;
import lox.parse.Stmt.ExprStmt;
import lox.parse.Stmt.IfStmt;
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

    @Test
    void shouldParseIfStatement() {
        Parser parser = new Parser("if (true) print 42;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(IfStmt.class);

        IfStmt ifStmt = (IfStmt) ast;
        assertThat(ifStmt.condition()).isInstanceOf(Group.class);
        assertThat(ifStmt.thenStmt()).isInstanceOf(PrintStmt.class);
        assertThat(ifStmt.elseStmt()).isEmpty();

        Group conditionGroup = (Group) ifStmt.condition();
        Terminal condition = (Terminal) conditionGroup.group();
        assertThat(condition.token().lexeme()).isEqualTo(Lexemes.TRUE);
    }

    @Test
    void shouldParseIfElseStatement() {
        Parser parser = new Parser("if (false) print 1; else print 2;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(IfStmt.class);

        IfStmt ifStmt = (IfStmt) ast;
        assertThat(ifStmt.condition()).isInstanceOf(Group.class);
        assertThat(ifStmt.thenStmt()).isInstanceOf(PrintStmt.class);
        assertThat(ifStmt.elseStmt()).isPresent();
        assertThat(ifStmt.elseStmt().get()).isInstanceOf(PrintStmt.class);

        Group conditionGroup = (Group) ifStmt.condition();
        Terminal condition = (Terminal) conditionGroup.group();
        assertThat(condition.token().lexeme()).isEqualTo(Lexemes.FALSE);
    }

    @Test
    void shouldParseIfWithBlockStatement() {
        Parser parser = new Parser("if (true) { print 42; }", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(IfStmt.class);

        IfStmt ifStmt = (IfStmt) ast;
        assertThat(ifStmt.condition()).isInstanceOf(Group.class);
        assertThat(ifStmt.thenStmt()).isInstanceOf(Block.class);
        assertThat(ifStmt.elseStmt()).isEmpty();

        Block thenBlock = (Block) ifStmt.thenStmt();
        assertThat(thenBlock.block()).hasSize(1);
        assertThat(thenBlock.block().get(0)).isInstanceOf(PrintStmt.class);
    }

    @Test
    void shouldParseIfElseWithBlockStatements() {
        Parser parser = new Parser("if (x > 0) { print \"positive\"; } else { print \"negative\"; }", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(IfStmt.class);

        IfStmt ifStmt = (IfStmt) ast;
        assertThat(ifStmt.condition()).isInstanceOf(Group.class);
        assertThat(ifStmt.thenStmt()).isInstanceOf(Block.class);
        assertThat(ifStmt.elseStmt()).isPresent();
        assertThat(ifStmt.elseStmt().get()).isInstanceOf(Block.class);

        Group conditionGroup = (Group) ifStmt.condition();
        Binary condition = (Binary) conditionGroup.group();
        assertThat(condition.op().lexeme()).isEqualTo(Lexemes.GREATER);
    }

    @Test
    void shouldParseNestedIfStatements() {
        Parser parser = new Parser("if (x > 0) if (y > 0) print \"both positive\";", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(IfStmt.class);

        IfStmt outerIf = (IfStmt) ast;
        assertThat(outerIf.condition()).isInstanceOf(Group.class);
        assertThat(outerIf.thenStmt()).isInstanceOf(IfStmt.class);
        assertThat(outerIf.elseStmt()).isEmpty();

        IfStmt innerIf = (IfStmt) outerIf.thenStmt();
        assertThat(innerIf.condition()).isInstanceOf(Group.class);
        assertThat(innerIf.thenStmt()).isInstanceOf(PrintStmt.class);
        assertThat(innerIf.elseStmt()).isEmpty();
    }

    @Test
    void shouldParseComplexIfCondition() {
        Parser parser = new Parser("if (x == 5) print \"equal\";", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        Ast ast = result.success().get(0);
        assertThat(ast).isInstanceOf(IfStmt.class);

        IfStmt ifStmt = (IfStmt) ast;
        assertThat(ifStmt.condition()).isInstanceOf(Group.class);
        assertThat(ifStmt.thenStmt()).isInstanceOf(PrintStmt.class);

        Group conditionGroup = (Group) ifStmt.condition();
        Binary condition = (Binary) conditionGroup.group();
        assertThat(condition.op().lexeme()).isEqualTo(Lexemes.EQUAL_EQUAL);
    }

    @Test
    void shouldHandleIfStatementWithMissingCondition() {
        Parser parser = new Parser("if print 42;", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
    }

    @Test
    void shouldHandleIfStatementWithMissingThen() {
        Parser parser = new Parser("if (true)", false);

        var result = parser.parse();

        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
    }

    @Test
    void shouldParseLogicalAndExpression() {
        Parser parser = new Parser("true and false;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical logical = (Logical) exprStmt.expr();
        assertThat(logical.op().lexeme()).isEqualTo(Lexemes.AND);
        assertThat(logical.left()).isInstanceOf(Terminal.class);
        assertThat(logical.right()).isInstanceOf(Terminal.class);

        Terminal left = (Terminal) logical.left();
        Terminal right = (Terminal) logical.right();
        assertThat(left.token().lexeme()).isEqualTo(Lexemes.TRUE);
        assertThat(right.token().lexeme()).isEqualTo(Lexemes.FALSE);
    }

    @Test
    void shouldParseLogicalOrExpression() {
        Parser parser = new Parser("false or true;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical logical = (Logical) exprStmt.expr();
        assertThat(logical.op().lexeme()).isEqualTo(Lexemes.OR);
        assertThat(logical.left()).isInstanceOf(Terminal.class);
        assertThat(logical.right()).isInstanceOf(Terminal.class);

        Terminal left = (Terminal) logical.left();
        Terminal right = (Terminal) logical.right();
        assertThat(left.token().lexeme()).isEqualTo(Lexemes.FALSE);
        assertThat(right.token().lexeme()).isEqualTo(Lexemes.TRUE);
    }

    @Test
    void shouldParseChainedLogicalExpressions() {
        Parser parser = new Parser("true and false or true;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        // Should parse as (true and false) or true due to precedence
        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical outerOr = (Logical) exprStmt.expr();
        assertThat(outerOr.op().lexeme()).isEqualTo(Lexemes.OR);
        assertThat(outerOr.left()).isInstanceOf(Logical.class);
        assertThat(outerOr.right()).isInstanceOf(Terminal.class);

        Logical innerAnd = (Logical) outerOr.left();
        assertThat(innerAnd.op().lexeme()).isEqualTo(Lexemes.AND);
    }

    @Test
    void shouldParseLogicalExpressionsWithParentheses() {
        Parser parser = new Parser("true and (false or true);", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical logical = (Logical) exprStmt.expr();
        assertThat(logical.op().lexeme()).isEqualTo(Lexemes.AND);
        assertThat(logical.left()).isInstanceOf(Terminal.class);
        assertThat(logical.right()).isInstanceOf(Group.class);

        Group group = (Group) logical.right();
        assertThat(group.group()).isInstanceOf(Logical.class);

        Logical innerLogical = (Logical) group.group();
        assertThat(innerLogical.op().lexeme()).isEqualTo(Lexemes.OR);
    }

    @Test
    void shouldParseLogicalExpressionsWithVariables() {
        Parser parser = new Parser("x and y or z;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical outerOr = (Logical) exprStmt.expr();
        assertThat(outerOr.op().lexeme()).isEqualTo(Lexemes.OR);
        assertThat(outerOr.right()).isInstanceOf(Terminal.class);

        Terminal rightVar = (Terminal) outerOr.right();
        assertThat(rightVar.token().lexeme()).isEqualTo(Lexemes.IDENTIFIER);
    }

    @Test
    void shouldParseComplexLogicalExpression() {
        Parser parser = new Parser("a and b or c and d;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        // Should parse as (a and b) or (c and d) due to precedence
        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical outerOr = (Logical) exprStmt.expr();
        assertThat(outerOr.op().lexeme()).isEqualTo(Lexemes.OR);
        assertThat(outerOr.left()).isInstanceOf(Logical.class);
        assertThat(outerOr.right()).isInstanceOf(Logical.class);

        Logical leftAnd = (Logical) outerOr.left();
        Logical rightAnd = (Logical) outerOr.right();
        assertThat(leftAnd.op().lexeme()).isEqualTo(Lexemes.AND);
        assertThat(rightAnd.op().lexeme()).isEqualTo(Lexemes.AND);
    }

    @Test
    void shouldParseLogicalExpressionsWithComparisons() {
        Parser parser = new Parser("x > 5 and y < 10;", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical logical = (Logical) exprStmt.expr();
        assertThat(logical.op().lexeme()).isEqualTo(Lexemes.AND);
        assertThat(logical.left()).isInstanceOf(Binary.class);
        assertThat(logical.right()).isInstanceOf(Binary.class);

        Binary leftComparison = (Binary) logical.left();
        Binary rightComparison = (Binary) logical.right();
        assertThat(leftComparison.op().lexeme()).isEqualTo(Lexemes.GREATER);
        assertThat(rightComparison.op().lexeme()).isEqualTo(Lexemes.LESS);
    }

    @Test
    void shouldParseLogicalExpressionsInIfStatements() {
        Parser parser = new Parser("if (x > 5 and y < 10) print \"valid\";", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        IfStmt ifStmt = (IfStmt) result.success().get(0);
        assertThat(ifStmt.condition()).isInstanceOf(Group.class);

        Group conditionGroup = (Group) ifStmt.condition();
        assertThat(conditionGroup.group()).isInstanceOf(Logical.class);

        Logical logical = (Logical) conditionGroup.group();
        assertThat(logical.op().lexeme()).isEqualTo(Lexemes.AND);
    }

    @Test
    void shouldParseNestedLogicalExpressions() {
        Parser parser = new Parser("(a or b) and (c or d);", false);

        var result = parser.parse();

        assertThat(result.isOk()).isTrue();
        assertThat(result.success()).hasSize(1);

        ExprStmt exprStmt = (ExprStmt) result.success().get(0);
        assertThat(exprStmt.expr()).isInstanceOf(Logical.class);

        Logical outerAnd = (Logical) exprStmt.expr();
        assertThat(outerAnd.op().lexeme()).isEqualTo(Lexemes.AND);
        assertThat(outerAnd.left()).isInstanceOf(Group.class);
        assertThat(outerAnd.right()).isInstanceOf(Group.class);

        Group leftGroup = (Group) outerAnd.left();
        Group rightGroup = (Group) outerAnd.right();
        assertThat(leftGroup.group()).isInstanceOf(Logical.class);
        assertThat(rightGroup.group()).isInstanceOf(Logical.class);

        Logical leftOr = (Logical) leftGroup.group();
        Logical rightOr = (Logical) rightGroup.group();
        assertThat(leftOr.op().lexeme()).isEqualTo(Lexemes.OR);
        assertThat(rightOr.op().lexeme()).isEqualTo(Lexemes.OR);
    }
}
