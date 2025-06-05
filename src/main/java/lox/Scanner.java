package lox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import lox.Tokens.Lexemes;
import lox.Tokens.TokenBuilder;
import lox.util.Tuple;
import lox.util.Tuples;

import static lox.util.LogUtil.trace;
import static lox.util.Util.matches;

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

  Result<List<Token>, List<Throwable>> scan(CharSequence source) {
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
          case ' ', '\t', '\r' -> null;
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
            // trace("found start of number: " + ch);
            // trace(chars.toString());

            StringBuilder sb = new StringBuilder();
            sb.append(ch);

            while (chars.hasNext() && matches(chars.peek(), (t) -> Character.isDigit(t) || t == '.')) {
              Character cur = chars.next();
              sb.append(cur);
              // trace("sb: " + sb);
              ++offset;

              // trace(chars.toString());

              if (cur == '.' && !matches(chars.peek(), Character::isDigit)) {
                // two '.' in a row e.g. 1..foo; so the number is '1.' and the 2nd '.' is a
                // separate token
                // trace("breaking at: " + chars);
                break;
              }
            }

            // trace(t.toString());
            yield new TokenBuilder(Lexemes.NUMBER).withValue(sb.toString());
          }
          case Character ch when Character.isLetter(ch) || ch == '_' -> {
            // trace("found start of keyword or identifier");
            StringBuilder sb = new StringBuilder();
            sb.append(ch);
            while (chars.hasNext()
                && matches(chars.peek(), (t) -> Character.isLetter(t) || Character.isDigit(t) || t == '_')) {
              Character next = chars.next();
              // trace("adding '" + next + "'");
              sb.append(next);
              ++offset;
            }

            var val = sb.toString();
            if (Lexemes.isKeyword(val)) {
              trace("found keyword: " + sb);
              yield new TokenBuilder(Lexemes.valueOf(val.toUpperCase()));
            } else {
              trace("found identifier: " + sb);
              yield new TokenBuilder(Lexemes.IDENTIFIER).withValue(val);
            }

          }
          default -> throw new UnexpectedCharacterException(c, Span.of(line, offset, 1));
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

  public interface PeekableIterator extends Iterator<Character>, Iterable<Character> {
    Optional<Character> peek();
  }

  public static class DefaultPeekableIterator implements PeekableIterator {
    private final CharSequence in;
    private int offset;

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
      return in.charAt(offset++);
    }

    @Override
    public Optional<Character> peek() {
      return Optional.ofNullable(offset < in.length() ? in.charAt(offset) : null);
    }

    @Override
    public String toString() {
      return "PeekableIterator[next() = " + ((offset < in.length()) ? in.charAt(offset) : "null") + "]";
    }

  }
}
