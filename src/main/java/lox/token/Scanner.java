package lox.token;

import static lox.util.LogUtil.trace;
import static lox.util.Util.matches;

import java.util.ArrayList;
import java.util.List;
import lox.Result;
import lox.Span;
import lox.token.Tokens.Lexemes;
import lox.token.Tokens.TokenBuilder;
import lox.util.CharSequencePeekableIterator;
import lox.util.PeekableIterator;
import lox.util.Tuple;
import lox.util.Tuples;

public class Scanner {

    Tuple thisOrThat(
        final PeekableIterator<Character> chars,
        final Character match,
        final Lexemes combo,
        final Lexemes single,
        final long offset
    ) {
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
        final var chars = new CharSequencePeekableIterator(source);
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
                        var res = thisOrThat(
                            chars,
                            '=',
                            Lexemes.LESS_EQUAL,
                            Lexemes.LESS,
                            offset
                        );

                        var tokenBuilder = (TokenBuilder) res.get(0);
                        offset = (long) res.get(1);

                        yield tokenBuilder;
                    }
                    case '>' -> {
                        var res = thisOrThat(
                            chars,
                            '=',
                            Lexemes.GREATER_EQUAL,
                            Lexemes.GREATER,
                            offset
                        );

                        var tokenBuilder = (TokenBuilder) res.get(0);
                        offset = (long) res.get(1);

                        yield tokenBuilder;
                    }
                    case '!' -> {
                        var res = thisOrThat(
                            chars,
                            '=',
                            Lexemes.BANG_EQUAL,
                            Lexemes.BANG,
                            offset
                        );

                        var tokenBuilder = (TokenBuilder) res.get(0);
                        offset = (long) res.get(1);

                        yield tokenBuilder;
                    }
                    case '=' -> {
                        var res = thisOrThat(
                            chars,
                            '=',
                            Lexemes.EQUAL_EQUAL,
                            Lexemes.EQUAL,
                            offset
                        );

                        var tokenBuilder = (TokenBuilder) res.get(0);
                        offset = (long) res.get(1);

                        yield tokenBuilder;
                    }
                    case '/' -> {
                        // check for line comment
                        if (matches(chars.peek(), '/')) {
                            while (
                                chars.hasNext() && !matches(chars.peek(), '\n')
                            ) {
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
                            throw new UnterminatedStringException(
                                sb.toString(),
                                Span.of(line, offset, 0)
                            );
                        }

                        var _n = chars.next(); // eat closing '"'
                        ++offset;

                        assert _n == '"' : "expected closing '\"'";
                        yield new TokenBuilder(Lexemes.STRING).withValue(
                            sb.toString()
                        );
                    }
                    case Character ch when Character.isDigit(ch) -> {
                        // trace("found start of number: " + ch);
                        // trace(chars.toString());

                        StringBuilder sb = new StringBuilder();
                        sb.append(ch);

                        while (
                            chars.hasNext() &&
                            matches(
                                chars.peek(),
                                t -> Character.isDigit(t) || t == '.'
                            )
                        ) {
                            Character cur = chars.next();
                            sb.append(cur);
                            // trace("sb: " + sb);
                            ++offset;

                            // trace(chars.toString());

                            if (
                                cur == '.' &&
                                !matches(chars.peek(), Character::isDigit)
                            ) {
                                // two '.' in a row e.g. 1..foo; so the number is '1.' and the 2nd '.' is a
                                // separate token
                                // trace("breaking at: " + chars);
                                break;
                            }
                        }

                        // trace(t.toString());
                        yield new TokenBuilder(Lexemes.NUMBER).withValue(
                            sb.toString()
                        );
                    }
                    case Character ch when (
                        Character.isLetter(ch) || ch == '_'
                    ) -> {
                        // trace("found start of keyword or identifier");
                        StringBuilder sb = new StringBuilder();
                        sb.append(ch);
                        while (
                            chars.hasNext() &&
                            matches(
                                chars.peek(),
                                t ->
                                    Character.isLetter(t) ||
                                    Character.isDigit(t) ||
                                    t == '_'
                            )
                        ) {
                            Character next = chars.next();
                            // trace("adding '" + next + "'");
                            sb.append(next);
                            ++offset;
                        }

                        var val = sb.toString();
                        if (Lexemes.isKeyword(val)) {
                            trace("found keyword: " + sb);
                            yield new TokenBuilder(
                                Lexemes.valueOf(val.toUpperCase())
                            );
                        } else {
                            trace("found identifier: " + sb);
                            yield new TokenBuilder(
                                Lexemes.IDENTIFIER
                            ).withValue(val);
                        }
                    }
                    default -> throw new UnexpectedCharacterException(
                        c,
                        Span.of(line, offset, 1)
                    );
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

}
