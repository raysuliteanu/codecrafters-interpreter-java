package lox;

import java.io.Closeable;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static lox.Tokens.TokenBuilder;
import static lox.Tokens.Lexemes;

public class Parser {
  public List<Token> scan(Reader source) {
    var tokens = new ArrayList<Token>();
    try (PushbackLineNumberReader reader = new PushbackLineNumberReader(source)) {
      for (int c = reader.read(); c != -1; c = reader.read()) {
        long start = reader.offset() - 1;
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
          case '<' -> {
            int next = reader.read();
            if (next == '=') {
              yield new TokenBuilder(Lexemes.LESS_EQ);
            } else if (next != -1) {
              reader.pushback((char) next);
            }
            yield new TokenBuilder(Lexemes.LESS);
          }
          case '>' -> {
            int next = reader.read();
            if (next == '=') {
              yield new TokenBuilder(Lexemes.GREATER_EQ);
            } else if (next != -1) {
              reader.pushback((char) next);
            }
            yield new TokenBuilder(Lexemes.GREATER);
          }
          case '!' -> {
            int next = reader.read();
            if (next == '=') {
              yield new TokenBuilder(Lexemes.BANG_EQ);
            } else if (next != -1) {
              reader.pushback((char) next);
            }
            yield new TokenBuilder(Lexemes.BANG);
          }
          case '=' -> {
            int next = reader.read();
            if (next == '=') {
              yield new TokenBuilder(Lexemes.EQ_EQ);
            } else if (next != -1) {
              reader.pushback((char) next);
            }
            yield new TokenBuilder(Lexemes.EQ);
          }
          default -> throw new RuntimeException("unknown token: " + c);
        };

        long current = reader.offset();
        token.withSpan(reader.line(), start, current - start).build();
        tokens.add(token.build());
      }
    } catch (IOException e) {
      throw new LoxException(e);
    } catch (ParseException e) {
      System.err.println(e);
    }

    return tokens;
  }

  static class PushbackLineNumberReader implements Closeable {
    long line = 1;
    long offset;
    PushbackReader reader;

    PushbackLineNumberReader(Reader r) {
      reader = new PushbackReader(r);
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }

    public int read() throws IOException {
      int c = reader.read();
      if (c == -1) {
        return c;
      }

      ++offset;

      if (c == '\n') {
        ++line;
      }

      return c;
    }

    public void pushback(char c) throws IOException {
      reader.unread(new char[] { c });
      --offset;
    }

    public long line() {
      return line;
    }

    public long offset() {
      return offset;
    }
  }
}
