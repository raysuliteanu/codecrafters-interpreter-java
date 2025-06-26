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
}
