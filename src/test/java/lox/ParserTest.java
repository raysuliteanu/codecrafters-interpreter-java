package lox;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import lox.Tokens.Lexemes;
import static lox.Tokens.TokenBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {
  @Test
  void scanLitteral() {
    String input = "{}(),;+-*.";
    var tokens = new Parser().scan(new StringReader(input));
    assertThat(tokens.size()).isEqualTo(input.length());
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.LEFT_BRACE).build(),
        new TokenBuilder(Lexemes.RIGHT_BRACE).build(),
        new TokenBuilder(Lexemes.LEFT_PAREN).build(),
        new TokenBuilder(Lexemes.RIGHT_PAREN).build(),
        new TokenBuilder(Lexemes.COMMA).build(),
        new TokenBuilder(Lexemes.SEMICOLON).build(),
        new TokenBuilder(Lexemes.PLUS).build(),
        new TokenBuilder(Lexemes.MINUS).build(),
        new TokenBuilder(Lexemes.STAR).build(),
        new TokenBuilder(Lexemes.DOT).build());
  }
}
