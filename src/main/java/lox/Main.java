package lox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import lox.eval.NumberResult;
import lox.parse.Ast;
import lox.token.Scanner;
import lox.token.Token;

public class Main {

    enum Options {
        tokenize,
        parse,
        evaluate,
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ./your_program.sh <command> <filename>");
            System.err.println("Commands: tokenize, parse, evaluate");
            System.exit(1);
        }

        Options command = null;
        try {
            command = Options.valueOf(args[0]);
        } catch (Exception e) {
            System.err.println("Unknown command: " + args[0]);
            System.exit(1);
        }

        String filename = args[1];

        int rc = 0;
        Optional<String> fileContents;
        switch (command) {
            case tokenize:
                fileContents = readFile(filename);
                if (fileContents.isPresent()) {
                    Result<List<Token>, List<Throwable>> result = new Scanner().scan(fileContents.get());

                    if (result.hasErr()) {
                        rc = 65;
                        for (var error : result.error()) {
                            System.err.println(error);
                        }
                    }

                    if (result.isOk()) {
                        for (var token : result.success()) {
                            System.out.println(token);
                        }
                    }
                }
                System.out.println("EOF  null");
                break;
            case parse:
                fileContents = readFile(filename);
                if (fileContents.isPresent()) {
                    Result<List<Ast>, List<Throwable>> parse = new lox.parse.Parser(fileContents.get()).parse();
                    if (parse.hasErr()) {
                        rc = 65;
                        for (var error : parse.error()) {
                            System.err.println(error);
                        }
                    }

                    if (parse.isOk()) {
                        for (var ast : parse.success()) {
                            System.out.println(ast);
                        }
                    }
                }
                break;
            case evaluate:
                fileContents = readFile(filename);
                if (fileContents.isPresent()) {
                    var result = new lox.eval.Interpreter().evaluate(fileContents.get());

                    if (result.hasErr()) {
                        rc = 70;
                        for (var error : result.error()) {
                            System.err.println(error);
                        }
                    }

                    if (result.isOk()) {
                        System.out.println(((Optional) result.success()).get());
                    }
                }
                break;
        }

        System.exit(rc);
    }

    /**
     * Read file contents to a String.
     *
     * Read the file contents and return the data as a String. If the file is empty,
     * or there is an error reading the file, the Optional will be empty.
     */
    private static Optional<String> readFile(String filename) {
        String fileContents = null;
        try {
            Path path = Path.of(filename);
            if (Files.size(path) > 0) {
                fileContents = Files.readString(path);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return Optional.ofNullable(fileContents);
    }
}
