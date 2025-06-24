package lox.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NumberTokenTest {

    @Test
    void value() {
        var itoken = new IntegerToken("123");
        assertThat(itoken.value()).isEqualTo(123);

        var token = new DoubleToken("123.456");
        assertThat(token.value()).isEqualTo(123.456);

        token = new DoubleToken("123.");
        assertThat(token.value()).isEqualTo(123.0);
    }
}
