package lox;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NumberTokenTest {

    @Test
    void value() {
        var token = new NumberToken("123");
        assertThat(token.value()).isEqualTo(123.0);

        token = new NumberToken("123.456");
        assertThat(token.value()).isEqualTo(123.456);

        token = new NumberToken("123.");
        assertThat(token.value()).isEqualTo(123.0);
     }
}
