package lox.parse;

import lox.token.Scanner;
import lox.token.Token;

public class Parser {

    private final Scanner scanner;
    private final CharSequence source;

    public Parser(final CharSequence source) {
        this.scanner = new Scanner();
        this.source = source;
    }

    public void parse() {
        var scanResult = scanner.scan(source);
    }
}
