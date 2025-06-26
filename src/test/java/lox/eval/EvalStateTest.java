package lox.eval;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvalStateTest {
    private EvalState evalState;

    @BeforeEach
    void setUp() {
        evalState = new EvalState();
    }

    @Test
    void shouldInitializeWithEmptyGlobalScope() {
        assertThatThrownBy(() -> evalState.variable("nonexistent"))
                .isInstanceOf(UndefinedVarException.class)
                .hasMessage("Undefined variable 'nonexistent'.");
    }

    @Test
    void shouldAddVariableToCurrentScope() {
        StringResult value = new StringResult("hello");
        
        evalState.addVariable("greeting", value);
        
        EvaluationResult<?> result = evalState.variable("greeting");
        assertThat(result).isEqualTo(value);
        assertThat(result.value()).isEqualTo("hello");
    }

    @Test
    void shouldAddNilWhenVariableValueIsNull() {
        evalState.addVariable("nullVar", null);
        
        EvaluationResult<?> result = evalState.variable("nullVar");
        assertThat(result).isInstanceOf(NilResult.class);
        assertThat(result.value()).isNull();
    }

    @Test
    void shouldAddVariableWithDifferentTypes() {
        evalState.addVariable("stringVar", new StringResult("test"));
        evalState.addVariable("numberVar", new DoubleResult(42.0));
        evalState.addVariable("boolVar", new BooleanResult(true));
        evalState.addVariable("nilVar", new NilResult());
        
        assertThat(evalState.variable("stringVar").value()).isEqualTo("test");
        assertThat(evalState.variable("numberVar").value()).isEqualTo(42.0);
        assertThat(evalState.variable("boolVar").value()).isEqualTo(true);
        assertThat(evalState.variable("nilVar").value()).isNull();
    }

    @Test
    void shouldUpdateExistingVariable() {
        StringResult originalValue = new StringResult("original");
        StringResult newValue = new StringResult("updated");
        
        evalState.addVariable("var", originalValue);
        EvaluationResult<?> returned = evalState.updateVariable("var", newValue);
        
        assertThat(returned).isEqualTo(originalValue);
        assertThat(evalState.variable("var")).isEqualTo(newValue);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentVariable() {
        StringResult value = new StringResult("test");
        
        assertThatThrownBy(() -> evalState.updateVariable("nonexistent", value))
                .isInstanceOf(UndefinedVarException.class)
                .hasMessage("Undefined variable 'nonexistent'.");
    }

    @Test
    void shouldCreateNewScopeWithPush() {
        evalState.addVariable("global", new StringResult("global value"));
        
        evalState.push();
        evalState.addVariable("local", new StringResult("local value"));
        
        assertThat(evalState.variable("global").value()).isEqualTo("global value");
        assertThat(evalState.variable("local").value()).isEqualTo("local value");
    }

    @Test
    void shouldShadowVariableInNestedScope() {
        evalState.addVariable("var", new StringResult("global"));
        
        evalState.push();
        evalState.addVariable("var", new StringResult("local"));
        
        assertThat(evalState.variable("var").value()).isEqualTo("local");
    }

    @Test
    void shouldRestoreVariableAfterPop() {
        evalState.addVariable("var", new StringResult("global"));
        
        evalState.push();
        evalState.addVariable("var", new StringResult("local"));
        assertThat(evalState.variable("var").value()).isEqualTo("local");
        
        evalState.pop();
        assertThat(evalState.variable("var").value()).isEqualTo("global");
    }

    @Test
    void shouldRemoveLocalVariablesAfterPop() {
        evalState.addVariable("global", new StringResult("global"));
        
        evalState.push();
        evalState.addVariable("local", new StringResult("local"));
        
        evalState.pop();
        
        assertThat(evalState.variable("global").value()).isEqualTo("global");
        assertThatThrownBy(() -> evalState.variable("local"))
                .isInstanceOf(UndefinedVarException.class);
    }

    @Test
    void shouldUpdateVariableInCorrectScope() {
        evalState.addVariable("var", new StringResult("global"));
        
        evalState.push();
        evalState.addVariable("localVar", new StringResult("local"));
        
        evalState.updateVariable("var", new StringResult("updated global"));
        evalState.updateVariable("localVar", new StringResult("updated local"));
        
        assertThat(evalState.variable("var").value()).isEqualTo("updated global");
        assertThat(evalState.variable("localVar").value()).isEqualTo("updated local");
        
        evalState.pop();
        assertThat(evalState.variable("var").value()).isEqualTo("updated global");
    }

    @Test
    void shouldHandleMultipleNestedScopes() {
        evalState.addVariable("level0", new StringResult("global"));
        
        evalState.push();
        evalState.addVariable("level1", new StringResult("first nested"));
        
        evalState.push();
        evalState.addVariable("level2", new StringResult("second nested"));
        
        evalState.push();
        evalState.addVariable("level3", new StringResult("third nested"));
        
        assertThat(evalState.variable("level0").value()).isEqualTo("global");
        assertThat(evalState.variable("level1").value()).isEqualTo("first nested");
        assertThat(evalState.variable("level2").value()).isEqualTo("second nested");
        assertThat(evalState.variable("level3").value()).isEqualTo("third nested");
        
        evalState.pop();
        assertThatThrownBy(() -> evalState.variable("level3"))
                .isInstanceOf(UndefinedVarException.class);
        
        evalState.pop();
        assertThatThrownBy(() -> evalState.variable("level2"))
                .isInstanceOf(UndefinedVarException.class);
        
        evalState.pop();
        assertThatThrownBy(() -> evalState.variable("level1"))
                .isInstanceOf(UndefinedVarException.class);
        
        assertThat(evalState.variable("level0").value()).isEqualTo("global");
    }

    @Test
    void shouldSearchScopesFromInnerToOuter() {
        evalState.addVariable("var", new StringResult("global"));
        
        evalState.push();
        evalState.push();
        evalState.push();
        
        assertThat(evalState.variable("var").value()).isEqualTo("global");
        
        evalState.addVariable("var", new StringResult("innermost"));
        assertThat(evalState.variable("var").value()).isEqualTo("innermost");
    }

    @Test
    void shouldHandleEmptyVariableNames() {
        evalState.addVariable("", new StringResult("empty name"));
        
        assertThat(evalState.variable("").value()).isEqualTo("empty name");
        
        evalState.updateVariable("", new StringResult("updated empty"));
        assertThat(evalState.variable("").value()).isEqualTo("updated empty");
    }

    @Test
    void shouldHandleVariableNameWithSpecialCharacters() {
        evalState.addVariable("var_with_underscore", new StringResult("underscore"));
        evalState.addVariable("var123", new StringResult("with numbers"));
        
        assertThat(evalState.variable("var_with_underscore").value()).isEqualTo("underscore");
        assertThat(evalState.variable("var123").value()).isEqualTo("with numbers");
    }

    @Test
    void shouldMaintainSeparateVariablesWithSimilarNames() {
        evalState.addVariable("var", new StringResult("var"));
        evalState.addVariable("variable", new StringResult("variable"));
        evalState.addVariable("var1", new StringResult("var1"));
        
        assertThat(evalState.variable("var").value()).isEqualTo("var");
        assertThat(evalState.variable("variable").value()).isEqualTo("variable");
        assertThat(evalState.variable("var1").value()).isEqualTo("var1");
    }
}