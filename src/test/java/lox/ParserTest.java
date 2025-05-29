package lox;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import lox.Tokens.Lexemes;
import static lox.Tokens.TokenBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {
  @Test
  void scanSingleCharLitteral() {
    String input = "{}(),;+-*.=<>!";
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
        new TokenBuilder(Lexemes.DOT).build(),
        new TokenBuilder(Lexemes.EQ).build(),
        new TokenBuilder(Lexemes.LESS).build(),
        new TokenBuilder(Lexemes.GREATER).build(),
        new TokenBuilder(Lexemes.BANG).build());

    for (int i = 0; i < input.length(); i++) {
      Span span = tokens.get(i).span();
      assertThat(span.sourceSpan().offset()).isEqualTo(i);
      assertThat(span.sourceSpan().length()).isEqualTo(1);
      assertThat(span.line()).isEqualTo(1);
    }
  }

  @Test
  void scanTwoCharLitteral() {
    String input = "==<=>=!=";
    var tokens = new Parser().scan(new StringReader(input));
    assertThat(tokens.size()).isEqualTo(4);
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.EQ_EQ).build(),
        new TokenBuilder(Lexemes.LESS_EQ).build(),
        new TokenBuilder(Lexemes.GREATER_EQ).build(),
        new TokenBuilder(Lexemes.BANG_EQ).build());

    long expectedOffset = 0;
    for (Token t : tokens) {
      Span span = t.span();
      assertThat(span.sourceSpan().offset()).isEqualTo(expectedOffset);
      long length = span.sourceSpan().length();
      assertThat(length).isEqualTo(2);
      assertThat(span.line()).isEqualTo(1);
      expectedOffset += length;
    }
  }
}
