package lox.token;

import lox.token.Tokens.Lexemes;

public class IntegerToken extends NumberToken<Integer> {
    IntegerToken(String original) {
        super(original, Integer.parseInt(original));
    }
}
