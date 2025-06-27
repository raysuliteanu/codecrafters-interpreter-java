package lox.eval;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

class InterpreterTest {

    @Test
    void shouldEvaluateNumberLiterals() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("42");
        
        assertThat(result.isOk()).isTrue();
        assertThat(result.error()).isEmpty();
        
        Optional<EvaluationResult<?>> evalResult = result.success();
        assertThat(evalResult).isPresent();
        assertThat(evalResult.get()).isInstanceOf(DoubleResult.class);
        
        DoubleResult doubleResult = (DoubleResult) evalResult.get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateStringLiterals() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("\"hello\"");
        
        assertThat(result.isOk()).isTrue();
        
        Optional<EvaluationResult<?>> evalResult = result.success();
        assertThat(evalResult).isPresent();
        assertThat(evalResult.get()).isInstanceOf(StringResult.class);
        
        StringResult stringResult = (StringResult) evalResult.get();
        assertThat(stringResult.value()).isEqualTo("hello");
    }

    @Test
    void shouldEvaluateBooleanLiterals() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        
        var result1 = interpreter1.evaluate("true");
        var result2 = interpreter2.evaluate("false");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        
        BooleanResult boolResult1 = (BooleanResult) result1.success().get();
        BooleanResult boolResult2 = (BooleanResult) result2.success().get();
        
        assertThat(boolResult1.value()).isTrue();
        assertThat(boolResult2.value()).isFalse();
    }

    @Test
    void shouldEvaluateNilLiteral() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("nil");
        
        assertThat(result.isOk()).isTrue();
        
        Optional<EvaluationResult<?>> evalResult = result.success();
        assertThat(evalResult).isPresent();
        assertThat(evalResult.get()).isInstanceOf(NilResult.class);
    }

    @Test
    void shouldEvaluateGroupedExpressions() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("(42)");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateUnaryMinusExpression() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("-42");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(-42.0);
    }

    @Test
    void shouldEvaluateUnaryBangExpression() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        Interpreter interpreter3 = new Interpreter(true);
        Interpreter interpreter4 = new Interpreter(true);
        
        var result1 = interpreter1.evaluate("!true");
        var result2 = interpreter2.evaluate("!false");
        var result3 = interpreter3.evaluate("!nil");
        var result4 = interpreter4.evaluate("!42");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        assertThat(result4.isOk()).isTrue();
        
        BooleanResult boolResult1 = (BooleanResult) result1.success().get();
        BooleanResult boolResult2 = (BooleanResult) result2.success().get();
        BooleanResult boolResult3 = (BooleanResult) result3.success().get();
        BooleanResult boolResult4 = (BooleanResult) result4.success().get();
        
        assertThat(boolResult1.value()).isFalse();
        assertThat(boolResult2.value()).isTrue();
        assertThat(boolResult3.value()).isTrue();
        assertThat(boolResult4.value()).isFalse();
    }

    @Test
    void shouldEvaluateArithmeticExpressions() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        Interpreter interpreter3 = new Interpreter(true);
        Interpreter interpreter4 = new Interpreter(true);
        
        var result1 = interpreter1.evaluate("1 + 2");
        var result2 = interpreter2.evaluate("5 - 3");
        var result3 = interpreter3.evaluate("4 * 6");
        var result4 = interpreter4.evaluate("10 / 2");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        assertThat(result4.isOk()).isTrue();
        
        DoubleResult doubleResult1 = (DoubleResult) result1.success().get();
        DoubleResult doubleResult2 = (DoubleResult) result2.success().get();
        DoubleResult doubleResult3 = (DoubleResult) result3.success().get();
        DoubleResult doubleResult4 = (DoubleResult) result4.success().get();
        
        assertThat(doubleResult1.value()).isEqualTo(3.0);
        assertThat(doubleResult2.value()).isEqualTo(2.0);
        assertThat(doubleResult3.value()).isEqualTo(24.0);
        assertThat(doubleResult4.value()).isEqualTo(5.0);
    }

    @Test
    void shouldEvaluateStringConcatenation() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("\"hello\" + \" world\"");
        
        assertThat(result.isOk()).isTrue();
        
        StringResult stringResult = (StringResult) result.success().get();
        assertThat(stringResult.value()).isEqualTo("hello world");
    }

    @Test
    void shouldEvaluateComparisonExpressions() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        Interpreter interpreter3 = new Interpreter(true);
        Interpreter interpreter4 = new Interpreter(true);
        
        var result1 = interpreter1.evaluate("1 < 2");
        var result2 = interpreter2.evaluate("2 <= 2");
        var result3 = interpreter3.evaluate("3 > 2");
        var result4 = interpreter4.evaluate("2 >= 2");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        assertThat(result4.isOk()).isTrue();
        
        BooleanResult boolResult1 = (BooleanResult) result1.success().get();
        BooleanResult boolResult2 = (BooleanResult) result2.success().get();
        BooleanResult boolResult3 = (BooleanResult) result3.success().get();
        BooleanResult boolResult4 = (BooleanResult) result4.success().get();
        
        assertThat(boolResult1.value()).isTrue();
        assertThat(boolResult2.value()).isTrue();
        assertThat(boolResult3.value()).isTrue();
        assertThat(boolResult4.value()).isTrue();
    }

    @Test
    void shouldEvaluateEqualityExpressions() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        Interpreter interpreter3 = new Interpreter(true);
        Interpreter interpreter4 = new Interpreter(true);
        
        var result1 = interpreter1.evaluate("42 == 42");
        var result2 = interpreter2.evaluate("42 != 43");
        var result3 = interpreter3.evaluate("\"hello\" == \"hello\"");
        var result4 = interpreter4.evaluate("true == true");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        assertThat(result4.isOk()).isTrue();
        
        BooleanResult boolResult1 = (BooleanResult) result1.success().get();
        BooleanResult boolResult2 = (BooleanResult) result2.success().get();
        BooleanResult boolResult3 = (BooleanResult) result3.success().get();
        BooleanResult boolResult4 = (BooleanResult) result4.success().get();
        
        assertThat(boolResult1.value()).isTrue();
        assertThat(boolResult2.value()).isTrue();
        assertThat(boolResult3.value()).isTrue();
        assertThat(boolResult4.value()).isTrue();
    }

    @Test
    void shouldEvaluateComplexExpressionWithPrecedence() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("1 + 2 * 3");
        
        assertThat(result.isOk()).isTrue();
        
        // Should evaluate as 1 + (2 * 3) = 7
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(7.0);
    }

    @Test
    void shouldEvaluateVariableDeclarationWithoutInitializer() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x;");
        
        assertThat(result.hasErr()).isFalse();
        assertThat(result.success()).isEmpty(); // var declaration returns null
    }

    @Test
    void shouldEvaluateVariableDeclarationWithInitializer() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 42;");
        
        assertThat(result.hasErr()).isFalse();
        assertThat(result.success()).isEmpty(); // var declaration returns null
    }

    @Test
    void shouldEvaluateVariableAccess() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        Optional<EvaluationResult<?>> evalResult = result.success();
        assertThat(evalResult).isPresent();
        assertThat(evalResult.get()).isInstanceOf(DoubleResult.class);
        
        DoubleResult doubleResult = (DoubleResult) evalResult.get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateVariableAssignment() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 10; x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateBlockScoping() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 10; { var x = 20; } x;");
        
        assertThat(result.isOk()).isTrue();
        
        // Outer x should still be 10
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(10.0);
    }

    @Test
    void shouldEvaluateExpressionStatements() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("42;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldHandleExpressionMode() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("1 + 2");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(3.0);
    }

    @Test
    void shouldHandleStatementMode() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("1 + 2;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(3.0);
    }

    @Test
    void shouldHandleMultipleStatements() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 1; var y = 2; x + y;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(3.0);
    }

    @Test
    void shouldHandleParseErrors() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("42 42;");
        
        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
    }

    @Test
    void shouldHandleInvalidUnaryOperation() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("-\"hello\"");
        
        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(EvalException.class);
    }

    @Test
    void shouldHandleInvalidBinaryOperation() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("42 + \"hello\"");
        
        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(EvalException.class);
    }

    @Test
    void shouldHandleUndefinedVariableAccess() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("undefinedVar");
        
        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(UndefinedVarException.class);
    }

    @Test
    void shouldHandleUndefinedVariableAssignment() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("undefinedVar = 42;");
        
        assertThat(result.hasErr()).isTrue();
        assertThat(result.error()).isNotEmpty();
        assertThat(result.error().get(0)).isInstanceOf(UndefinedVarException.class);
    }

    @Test
    void shouldHandleEmptyInput() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("");
        
        assertThat(result.hasErr()).isFalse();
        assertThat(result.success()).isEmpty();
    }

    @Test
    void shouldHandleWhitespaceOnlyInput() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("   \n\t  ");
        
        assertThat(result.hasErr()).isFalse();
        assertThat(result.success()).isEmpty();
    }

    @Test
    void shouldHandleComments() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("// this is a comment\n42;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateNestedExpressions() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("((1 + 2) * 3) - 4");
        
        assertThat(result.isOk()).isTrue();
        
        // Should evaluate as ((1 + 2) * 3) - 4 = (3 * 3) - 4 = 9 - 4 = 5
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(5.0);
    }

    @Test
    void shouldEvaluateChainedAssignments() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x; var y; x = y = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateComplexBlockWithNestedScopes() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 1; { var y = 2; { var z = 3; x + y + z; } }");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(6.0);
    }

    @Test
    void shouldEvaluateIfStatementWithTrueBooleanCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (true) x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfStatementWithFalseBooleanCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (false) x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(0.0);
    }

    @Test
    void shouldEvaluateIfElseStatementWithTrueCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (true) x = 42; else x = 99; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfElseStatementWithFalseCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (false) x = 42; else x = 99; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(99.0);
    }

    @Test
    void shouldEvaluateIfStatementWithComparisonCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (5 > 3) x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfStatementWithEqualityCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (10 == 10) x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfStatementWithVariableCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var condition = true; var x = 0; if (condition) x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfStatementWithBlockStatement() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (true) { x = 42; var y = 10; x = x + y; } x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(52.0);
    }

    @Test
    void shouldEvaluateIfElseWithBlockStatements() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (false) { x = 42; } else { x = 99; var z = 1; x = x + z; } x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(100.0);
    }

    @Test
    void shouldEvaluateNestedIfStatements() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 0; if (true) if (true) x = 42; x;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfStatementWithScopedVariables() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 10; if (true) { var x = 20; } x;");
        
        assertThat(result.isOk()).isTrue();
        
        // Outer x should still be 10
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(10.0);
    }

    @Test
    void shouldHandleIfStatementWithNonBooleanCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("if (42) print \"hello\";");
        
        // Non-boolean values are truthy in Lox (42 is truthy)
        assertThat(result.hasErr()).isFalse();
    }

    @Test
    void shouldHandleIfStatementWithStringCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("if (\"hello\") print \"world\";");
        
        // Strings are truthy in Lox ("hello" is truthy)
        assertThat(result.hasErr()).isFalse();
    }

    @Test
    void shouldHandleIfStatementWithNilCondition() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("if (nil) print \"world\";");
        
        // nil is falsy in Lox, so the if statement doesn't execute
        assertThat(result.hasErr()).isFalse();
        assertThat(result.success()).isEmpty(); // if with falsy condition that doesn't execute returns null
    }

    @Test
    void shouldEvaluateIfStatementReturningFromThenBranch() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("if (true) 42; else 99;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateIfStatementReturningFromElseBranch() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("if (false) 42; else 99;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(99.0);
    }

    @Test
    void shouldEvaluateIfStatementWithoutElseReturningNull() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("if (false) 42;");
        
        assertThat(result.hasErr()).isFalse();
        assertThat(result.success()).isEmpty(); // if without else that doesn't execute returns null
    }

    @Test
    void shouldEvaluateLogicalAndWithTrueLeft() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("true and false");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isFalse();
    }

    @Test
    void shouldEvaluateLogicalAndWithFalseLeft() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("false and true");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isFalse();
    }

    @Test
    void shouldEvaluateLogicalOrWithTrueLeft() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("true or false");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateLogicalOrWithFalseLeft() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("false or true");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldShortCircuitLogicalAndWithFalseLeft() {
        Interpreter interpreter = new Interpreter(false);
        
        // This should short-circuit and not evaluate the undefined variable
        var result = interpreter.evaluate("false and undefinedVar;");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isFalse();
    }

    @Test
    void shouldShortCircuitLogicalOrWithTrueLeft() {
        Interpreter interpreter = new Interpreter(false);
        
        // This should short-circuit and not evaluate the undefined variable
        var result = interpreter.evaluate("true or undefinedVar;");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateLogicalAndWithNonBooleanValues() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("42 and \"hello\"");
        
        assertThat(result.isOk()).isTrue();
        
        // In Lox, logical operators return the last evaluated value
        StringResult stringResult = (StringResult) result.success().get();
        assertThat(stringResult.value()).isEqualTo("hello");
    }

    @Test
    void shouldEvaluateLogicalOrWithNonBooleanValues() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("nil or 42");
        
        assertThat(result.isOk()).isTrue();
        
        // nil is falsy, so should return 42
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldEvaluateChainedLogicalExpressions() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("true and false or true");
        
        assertThat(result.isOk()).isTrue();
        
        // Should evaluate as (true and false) or true = false or true = true
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateComplexLogicalExpression() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 5; var y = 10; x > 3 and y < 15;");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateLogicalExpressionsWithParentheses() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("true and (false or true)");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateLogicalAndWithVariables() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var a = true; var b = false; a and b;");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isFalse();
    }

    @Test
    void shouldEvaluateLogicalOrWithVariables() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var a = false; var b = true; a or b;");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateLogicalExpressionsInIfStatements() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = 5; var y = 10; var result = 0; if (x > 3 and y < 15) result = 42; result;");
        
        assertThat(result.isOk()).isTrue();
        
        DoubleResult doubleResult = (DoubleResult) result.success().get();
        assertThat(doubleResult.value()).isEqualTo(42.0);
    }

    @Test
    void shouldHandleLogicalExpressionsWithNilAndNumbers() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        Interpreter interpreter3 = new Interpreter(true);
        
        var result1 = interpreter1.evaluate("nil and 42");
        var result2 = interpreter2.evaluate("0 and true");
        var result3 = interpreter3.evaluate("nil or \"default\"");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        
        // nil is falsy, so "nil and 42" should return nil
        NilResult nilResult = (NilResult) result1.success().get();
        
        // 0 is truthy in Lox, so "0 and true" should return true
        BooleanResult boolResult = (BooleanResult) result2.success().get();
        assertThat(boolResult.value()).isTrue();
        
        // nil is falsy, so "nil or \"default\"" should return "default"
        StringResult stringResult = (StringResult) result3.success().get();
        assertThat(stringResult.value()).isEqualTo("default");
    }

    @Test
    void shouldEvaluateComplexNestedLogicalExpressions() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var a = true; var b = false; var c = true; var d = false; (a or b) and (c or d);");
        
        assertThat(result.isOk()).isTrue();
        
        // (true or false) and (true or false) = true and true = true
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldEvaluateLogicalOperatorPrecedence() {
        Interpreter interpreter = new Interpreter(true);
        
        var result = interpreter.evaluate("false or true and false");
        
        assertThat(result.isOk()).isTrue();
        
        // Should evaluate as false or (true and false) = false or false = false
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isFalse();
    }

    @Test
    void shouldEvaluateLogicalExpressionsWithAssignments() {
        Interpreter interpreter = new Interpreter(false);
        
        var result = interpreter.evaluate("var x = false; var y = false; x = true and (y = true); x;");
        
        assertThat(result.isOk()).isTrue();
        
        BooleanResult boolResult = (BooleanResult) result.success().get();
        assertThat(boolResult.value()).isTrue();
    }

    @Test
    void shouldReturnCorrectValueFromLogicalOperators() {
        Interpreter interpreter1 = new Interpreter(true);
        Interpreter interpreter2 = new Interpreter(true);
        Interpreter interpreter3 = new Interpreter(true);
        
        // Logical operators should return the last evaluated operand
        var result1 = interpreter1.evaluate("\"first\" and \"second\"");
        var result2 = interpreter2.evaluate("nil or \"default\"");
        var result3 = interpreter3.evaluate("42 and nil");
        
        assertThat(result1.isOk()).isTrue();
        assertThat(result2.isOk()).isTrue();
        assertThat(result3.isOk()).isTrue();
        
        StringResult stringResult1 = (StringResult) result1.success().get();
        assertThat(stringResult1.value()).isEqualTo("second");
        
        StringResult stringResult2 = (StringResult) result2.success().get();
        assertThat(stringResult2.value()).isEqualTo("default");
        
        NilResult nilResult = (NilResult) result3.success().get();
        // 42 is truthy, so it evaluates the right side and returns nil
    }
}
