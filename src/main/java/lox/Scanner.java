package lox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static lox.Tokens.TokenBuilder;
import static lox.Tokens.Lexemes;

public class Scanner {
  boolean matches(Optional<Character> oc, Character c) {
    return oc.isPresent() && oc.get() == c;
  }

  public List<Token> scan(CharSequence source) {
    final var tokens = new ArrayList<Token>();
    final var chars = new DefaultPeekableIterator(source);
    try {
      long offset = 0;
      long line = 1;
      for (var c : chars) {
        long tokenStart = offset++;

        TokenBuilder token = switch (c) {
          case ' ', '\t', '\r' -> {
            yield null;
          }
          case '\n' -> {
            ++line;
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
          case '<' -> {
            if (matches(chars.peek(), '=')) {
              chars.next(); // eat matching '='
              ++offset;
              yield new TokenBuilder(Lexemes.LESS_EQ);
            } else {
              yield new TokenBuilder(Lexemes.LESS);
            }
          }
          case '>' -> {
            if (matches(chars.peek(), '=')) {
              chars.next(); // eat matching '='
              ++offset;
              yield new TokenBuilder(Lexemes.GREATER_EQ);
            } else {
              yield new TokenBuilder(Lexemes.GREATER);
            }
          }
          case '!' -> {
            if (matches(chars.peek(), '=')) {
              chars.next(); // eat matching '='
              ++offset;
              yield new TokenBuilder(Lexemes.BANG_EQ);
            } else {
              yield new TokenBuilder(Lexemes.BANG);
            }
          }
          case '=' -> {
            if (matches(chars.peek(), '=')) {
              chars.next(); // eat matching '='
              ++offset;
              yield new TokenBuilder(Lexemes.EQ_EQ);
            } else {
              yield new TokenBuilder(Lexemes.EQ);
            }
          }
          case '/' -> {
            // check for line comment
            if (matches(chars.peek(), '/')) {
              while (!matches(chars.peek(), '\n')) {
                chars.next(); // eat matching '='
              }
              yield null;
            } else {
              yield new TokenBuilder(Lexemes.SLASH);
            }
          }
          default -> throw new UnexpectedTokenException(c, Span.of(line, offset, 1));
        };

        if (token != null) {
          token.withSpan(line, tokenStart, offset - tokenStart).build();
          tokens.add(token.build());
        }
      }
    } catch (ParseException e) {
      System.err.println(e);
    }

    return tokens;
  }

  public static interface PeekableIterator extends Iterator<Character>, Iterable<Character> {
    Optional<Character> peek();
  }

  public static class DefaultPeekableIterator implements PeekableIterator {
    private final CharSequence in;
    private int offset;
    private int peekOffset;

    public DefaultPeekableIterator(CharSequence in) {
      this.in = in;
    }

    @Override
    public Iterator<Character> iterator() {
      return this;
    }

    @Override
    public boolean hasNext() {
      return offset < in.length();
    }

    @Override
    public Character next() {
      peekOffset = offset;
      Character c = in.charAt(offset);
      ++offset;
      return c;
    }

    @Override
    public Optional<Character> peek() {
      ++peekOffset;
      return Optional.ofNullable(peekOffset < in.length() ? in.charAt(peekOffset) : null);
    }
  }
}
