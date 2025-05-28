package lox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringReader;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ScannerTest {
  @Test
  void scanLitteral() {
    String input = "{}(),;+-*.";
    var tokens = new Scanner().scan(new StringReader(input));
    assertThat(tokens.size()).isEqualTo(input.length());
    // assertThat(tokens).containsExactly(
    // new Tokens.LeftBrace(),
    // new Tokens.RightBrace(),
    // new Tokens.LeftParen(),
    // new Tokens.RightParen(),
    // new Tokens.Comma(),
    // new Tokens.Semicolon(),
    // new Tokens.Plus(),
    // new Tokens.Minus(),
    // new Tokens.Star(),
    // new Tokens.Dot());

    tokens.forEach(System.out::println);
  }
}
