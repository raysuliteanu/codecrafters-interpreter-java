package lox.token;

import lox.token.Tokens.Lexemes;

public class DoubleToken extends NumberToken<Double> {
    DoubleToken(String original) {
        String tmp = original;
        if (original.endsWith(".")) {
            tmp = original + "0";
        }

        super(original, Double.parseDouble(tmp));
    }
}
