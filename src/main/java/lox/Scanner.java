package lox;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static lox.Tokens.TokenBuilder;
import static lox.Tokens.Lexemes;

public class Scanner {
  public List<Token> scan(Reader source) {
    LineNumberReader reader = new LineNumberReader(source);
    reader.setLineNumber(1);

    var tokens = new ArrayList<Token>();
    try {
      var offset = 0L;
      for (int c = source.read(); c != -1; c = source.read()) {
        TokenBuilder token = switch (c) {
          case '\n', '\r' -> {
            yield null;
          }
          case '{' -> new TokenBuilder(Lexemes.LEFT_BRACE);
          case '}' -> new TokenBuilder(Lexemes.RIGHT_BRACE);
          case '(' -> new TokenBuilder(Lexemes.LEFT_PAREN);
          case ')' -> new TokenBuilder(Lexemes.RIGHT_PAREN);
          case ',' -> new TokenBuilder(Lexemes.COMMA);
          case '.' -> new TokenBuilder(Lexemes.DOT);
          case '+' -> new TokenBuilder(Lexemes.PLUS);
          case '-' -> new TokenBuilder(Lexemes.MINUS);
          case ';' -> new TokenBuilder(Lexemes.SEMICOLON);
          case '*' -> new TokenBuilder(Lexemes.STAR);
          default -> throw new RuntimeException();
        };

        token.withSpan(Span.of(reader.getLineNumber(), offset, 1)).build();
        tokens.add(token.build());
        ++offset;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return tokens;
  }
}
