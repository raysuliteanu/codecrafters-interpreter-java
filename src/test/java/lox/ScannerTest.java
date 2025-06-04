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
        new TokenBuilder(Lexemes.EQUAL).build(),
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
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.EQUAL_EQUAL).build(),
        new TokenBuilder(Lexemes.LESS_EQUAL).build(),
        new TokenBuilder(Lexemes.GREATER_EQUAL).build(),
        new TokenBuilder(Lexemes.BANG_EQUAL).build());

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
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.BANG_EQUAL).build(),
        new TokenBuilder(Lexemes.EQUAL_EQUAL).build(),
        new TokenBuilder(Lexemes.LESS_EQUAL).build(),
        new TokenBuilder(Lexemes.GREATER_EQUAL).build(),
        new TokenBuilder(Lexemes.LESS).build(),
        new TokenBuilder(Lexemes.GREATER).build(),
        new TokenBuilder(Lexemes.BANG).build(),
        new TokenBuilder(Lexemes.EQUAL).build());
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

  @Test
  void scanIntegerNumbers() {
    String input = "123 456 0 9";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.NUMBER).withValue("123").build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("456").build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("0").build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("9").build());
  }

  @Test
  void scanDecimalNumbers() {
    String input = "123.456 0.0 99.9";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.NUMBER).withValue("123.456").build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("0.0").build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("99.9").build());
  }

  @Test
  void scanNumbersWithSpanTracking() {
    String input = "42 3.14";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens.size()).isEqualTo(2);

    Token firstToken = tokens.getFirst();
    assertThat(firstToken.span().sourceSpan().offset()).isEqualTo(0);
    assertThat(firstToken.span().sourceSpan().length()).isEqualTo(2);
    assertThat(firstToken.span().line()).isEqualTo(1);

    Token secondToken = tokens.get(1);
    assertThat(secondToken.span().sourceSpan().offset()).isEqualTo(3);
    assertThat(secondToken.span().sourceSpan().length()).isEqualTo(4);
    assertThat(secondToken.span().line()).isEqualTo(1);
  }

  @Test
  void scanKeywords() {
    String input = "true false nil and or class for fun if else return super this var while print";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.TRUE).build(),
        new TokenBuilder(Lexemes.FALSE).build(),
        new TokenBuilder(Lexemes.NIL).build(),
        new TokenBuilder(Lexemes.AND).build(),
        new TokenBuilder(Lexemes.OR).build(),
        new TokenBuilder(Lexemes.CLASS).build(),
        new TokenBuilder(Lexemes.FOR).build(),
        new TokenBuilder(Lexemes.FUN).build(),
        new TokenBuilder(Lexemes.IF).build(),
        new TokenBuilder(Lexemes.ELSE).build(),
        new TokenBuilder(Lexemes.RETURN).build(),
        new TokenBuilder(Lexemes.SUPER).build(),
        new TokenBuilder(Lexemes.THIS).build(),
        new TokenBuilder(Lexemes.VAR).build(),
        new TokenBuilder(Lexemes.WHILE).build(),
        new TokenBuilder(Lexemes.PRINT).build());
  }

  @Test
  void scanIdentifiers() {
    String input = "variable_name foo bar123 _underscore camelCase";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("variable_name").build(),
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("foo").build(),
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("bar123").build(),
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("_underscore").build(),
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("camelCase").build());
  }

  @Test
  void scanMixedKeywordsAndIdentifiers() {
    String input = "var myVariable = true; if someCondition";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.VAR).build(),
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("myVariable").build(),
        new TokenBuilder(Lexemes.EQUAL).build(),
        new TokenBuilder(Lexemes.TRUE).build(),
        new TokenBuilder(Lexemes.SEMICOLON).build(),
        new TokenBuilder(Lexemes.IF).build(),
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("someCondition").build());
  }

  @Test
  void scanNumbersInExpressions() {
    String input = "x = 42 + 3.14 * 2";
    Result<List<Token>, List<Throwable>> result = new Scanner().scan(input);
    assertThat(result.isOk()).isTrue();
    var tokens = result.success();
    assertThat(tokens).containsExactly(
        new TokenBuilder(Lexemes.IDENTIFIER).withValue("x").build(),
        new TokenBuilder(Lexemes.EQUAL).build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("42").build(),
        new TokenBuilder(Lexemes.PLUS).build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("3.14").build(),
        new TokenBuilder(Lexemes.STAR).build(),
        new TokenBuilder(Lexemes.NUMBER).withValue("2").build());
  }
}
