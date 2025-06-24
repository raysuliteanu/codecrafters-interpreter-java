package lox.token;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumberTokenTest {

    @Test
    void value() {
        var token = new DoubleToken("123");
        assertThat(token.value()).isEqualTo(123);

        token = new DoubleToken("123.456");
        assertThat(token.value()).isEqualTo(123.456);

        token = new DoubleToken("123.");
        assertThat(token.value()).isEqualTo(123.0);
    }
}
