package lox;

import java.util.List;

import org.junit.jupiter.api.Test;

import lox.Tokens.Lexemes;
import static lox.Tokens.TokenBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ScannerTest {
  @Test
  void scanSingleCharLiteral() {
    String input = "{}(),;+-*.=<>!";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
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
  void scanTwoCharLiteral() {
    String input = "==<=>=!=";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
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

  @Test
  void scanWithWhitespace() {
    String input = "{ } ( )";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(4);
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.LEFT_BRACE).build(),
        new TokenBuilder(Lexemes.RIGHT_BRACE).build(),
        new TokenBuilder(Lexemes.LEFT_PAREN).build(),
        new TokenBuilder(Lexemes.RIGHT_PAREN).build());
  }

  @Test
  void scanWithNewlines() {
    String input = "{\n}\n(";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(3);
    assertThat(tokens.get(0).span().line()).isEqualTo(1);
    assertThat(tokens.get(1).span().line()).isEqualTo(2);
    assertThat(tokens.get(2).span().line()).isEqualTo(3);
  }

  @Test
  void scanLineComments() {
    String input = "// this is a comment\n+";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(1);
    assertThat(tokens.getFirst()).isEqualTo(new TokenBuilder(Lexemes.PLUS).build());
    assertThat(tokens.getFirst().span().line()).isEqualTo(2);
    assertThat(tokens.getFirst().span().sourceSpan().offset()).isEqualTo(input.indexOf('+'));
    assertThat(tokens.getFirst().span().sourceSpan().length()).isEqualTo(1);
  }

  @Test
  void scanSlashWithoutComment() {
    String input = "/ +";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(2);
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.SLASH).build(),
        new TokenBuilder(Lexemes.PLUS).build());
  }

  @Test
  void scanEmptyInput() {
    String input = "";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    var tokens = result.success();
    assertThat(tokens).isEmpty();
  }

  @Test
  void scanOnlyWhitespace() {
    String input = "   \t\r\n  ";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    var tokens = result.success();
    assertThat(tokens).isEmpty();
  }

  @Test
  void scanMixedOperators() {
    String input = "!= == <= >= < > ! =";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(8);
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.BANG_EQ).build(),
        new TokenBuilder(Lexemes.EQ_EQ).build(),
        new TokenBuilder(Lexemes.LESS_EQ).build(),
        new TokenBuilder(Lexemes.GREATER_EQ).build(),
        new TokenBuilder(Lexemes.LESS).build(),
        new TokenBuilder(Lexemes.GREATER).build(),
        new TokenBuilder(Lexemes.BANG).build(),
        new TokenBuilder(Lexemes.EQ).build());
  }

  @Test
  void scanCommentAtEndOfFile() {
    String input = "+ // comment at end";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(1);
    assertThat(tokens.getFirst()).isEqualTo(new TokenBuilder(Lexemes.PLUS).build());
  }
}
