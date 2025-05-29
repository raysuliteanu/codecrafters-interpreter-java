package lox;

import java.io.Closeable;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import static lox.Tokens.TokenBuilder;
import static lox.Tokens.Lexemes;

public class Parser {
  public List<Token> scan(Reader source) {
    PeekableLineNumberReader reader = new PeekableLineNumberReader(source);

    var tokens = new ArrayList<Token>();
    try {
      for (int c = reader.read(); c != -1; c = reader.read()) {
        TokenBuilder token = switch (c) {
          case '\n', '\r' -> null;
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
          default -> throw new RuntimeException("unknown token: " + c);
        };

        token.withSpan(Span.of(reader.line(), reader.offset(), 1)).build();
        tokens.add(token.build());
        ++offset;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return tokens;
  }

  static class PeekableLineNumberReader implements Closeable {
    long line;
    long offset;
    PushbackReader reader;

    PeekableLineNumberReader(Reader r) {
      this.reader = new PushbackReader(r);
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }

    public int read() throws IOException {
      int c = reader.read();
      if (c == -1)
        return c;

      ++offset;

      return c;
    }

    public long line() {
      return line;
    }

    public long offset() {
      return offset;
    }
  }
}
