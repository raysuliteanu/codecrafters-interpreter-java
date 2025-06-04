package lox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import lox.Scanner.DefaultPeekableIterator;
import lox.Scanner.PeekableIterator;
import lox.Tokens.Lexemes;
import lox.Tokens.TokenBuilder;

import static lox.LogUtil.trace;
import static lox.Util.matches;

public class Scanner {
  Tuple thisOrThat(final PeekableIterator chars, final Character match, final Lexemes combo, final Lexemes single,
      final long offset) {
    if (matches(chars.peek(), match)) {
      chars.next(); // eat matching '='
      return Tuples.of(new TokenBuilder(combo), offset + 1);
    } else {
      return Tuples.of(new TokenBuilder(single), offset);
    }
  }

  public Result<List<Token>, List<Throwable>> scan(CharSequence source) {
    final var tokens = new ArrayList<Token>();
    final var exceptions = new ArrayList<Throwable>();
    final var chars = new DefaultPeekableIterator(source);
    long offset = 0;
    long line = 1;

    for (var c : chars) {
      TokenBuilder token = null;
      long tokenStart = offset++;

      try {
        token = switch (c) {
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
            var res = thisOrThat(chars, '=', Lexemes.LESS_EQUAL, Lexemes.LESS, offset);

            var tokenBuilder = (TokenBuilder) res.get(0);
            offset = (long) res.get(1);

            yield tokenBuilder;
          }
          case '>' -> {
            var res = thisOrThat(chars, '=', Lexemes.GREATER_EQUAL, Lexemes.GREATER, offset);

            var tokenBuilder = (TokenBuilder) res.get(0);
            offset = (long) res.get(1);

            yield tokenBuilder;
          }
          case '!' -> {
            var res = thisOrThat(chars, '=', Lexemes.BANG_EQUAL, Lexemes.BANG, offset);

            var tokenBuilder = (TokenBuilder) res.get(0);
            offset = (long) res.get(1);

            yield tokenBuilder;
          }
          case '=' -> {
            var res = thisOrThat(chars, '=', Lexemes.EQUAL_EQUAL, Lexemes.EQUAL, offset);

            var tokenBuilder = (TokenBuilder) res.get(0);
            offset = (long) res.get(1);

            yield tokenBuilder;
          }
          case '/' -> {
            // check for line comment
            if (matches(chars.peek(), '/')) {
              while (chars.hasNext() && !matches(chars.peek(), '\n')) {
                chars.next(); // eat matching '='
                ++offset;
              }
              yield null;
            } else {
              yield new TokenBuilder(Lexemes.SLASH);
            }
          }
          case '"' -> {
            trace("found start of quoted string");
            // check for quoted string
            StringBuilder sb = new StringBuilder();
            while (chars.hasNext() && !matches(chars.peek(), '"')) {
              Character next = chars.next();
              // trace("adding '" + next + "'");
              sb.append(next);
              ++offset;
            }

            if (!chars.hasNext()) {
              throw new UnterminatedStringException(sb.toString(), Span.of(line, offset, 0));
            }

            var _n = chars.next(); // eat closing '"'
            ++offset;

            assert _n == '"' : "expected closing '\"'";
            yield new TokenBuilder(Lexemes.STRING).withValue(sb.toString());
          }
          case Character ch when Character.isDigit(ch) -> {
            trace("found start of number: " + ch);
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            while (chars.hasNext() && matches(chars.peek(), (t) -> Character.isDigit(t) || t == '.')) {
              Character next = chars.next();
              // trace("adding '" + next + "'");
              sb.append(next);
              ++offset;

              if (matches(chars.peek(), '.')) {
                // two '.' in a row e.g. 1..foo; so the number is '1.' and the 2nd '.' is a
                // separate token
                break;
              }
            }

            yield new TokenBuilder(Lexemes.NUMBER).withValue(sb.toString());
          }
          case Character ch when Character.isLetter(ch) -> {
            trace("found start of keyword or identifier");
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            while (chars.hasNext() && matches(chars.peek(), (t) -> Character.isLetter(t) || t == '_')) {
              Character next = chars.next();
              // trace("adding '" + next + "'");
              sb.append(next);
              ++offset;
            }

            var val = sb.toString();
            if (Lexemes.isKeyword(val.toUpperCase())) {
              trace("found keyword: " + sb);
              yield new TokenBuilder(Lexemes.valueOf(val.toUpperCase()));
            } else {
              trace("found identifier: " + sb);
              yield new TokenBuilder(Lexemes.IDENTIFIER).withValue(val);
            }

          }
          default -> {
            throw new UnexpectedCharacterException(c, Span.of(line, offset, 1));
          }
        };
      } catch (ParseException e) {
        exceptions.add(e);
      }

      if (token != null) {
        token.withSpan(line, tokenStart, offset - tokenStart).build();
        tokens.add(token.build());
      }
    }

    return new Result<>(tokens, exceptions);
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
