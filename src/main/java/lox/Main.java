package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lox.eval.EvalException;
import lox.eval.DoubleResult;
import lox.eval.Interpreter;
import lox.parse.Ast;
import lox.parse.ParseException;
import lox.parse.Parser;
import lox.token.Scanner;
import lox.token.Token;

public class Main {

    enum Options {
        tokenize,
        parse,
        evaluate,
        run,
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ./your_program.sh <command> [filename]");
            System.err.println("Commands: tokenize, parse, evaluate (require filename), run (no filename)");
            System.exit(1);
        }

        Options command = null;
        try {
            command = Options.valueOf(args[0]);
        } catch (Exception e) {
            System.err.println("Unknown command: " + args[0]);
            System.exit(1);
        }

        // Check if filename is required for this command
        if (command != Options.run && args.length < 2) {
            System.err.println("Usage: ./your_program.sh " + command + " <filename>");
            System.exit(1);
        }

        String filename = args.length > 1 ? args[1] : null;

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
                    Result<List<Ast>, List<Throwable>> parse = new Parser(fileContents.get(), true).parse();
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
                    var result = new Interpreter(true).evaluate(fileContents.get());

                    if (result.hasErr()) {
                        rc = determineErrorCode(result.error().stream());

                        for (var error : result.error()) {
                            System.err.println(error);
                        }
                    }

                    if (result.isOk()) {
                        System.out.println(((Optional) result.success()).get());
                    }
                }
                break;
            case run:
                if (filename != null) {
                    // Run file
                    fileContents = readFile(filename);
                    if (fileContents.isPresent()) {
                        var result = new Interpreter(false).evaluate(fileContents.get());

                        if (result.hasErr()) {
                            rc = determineErrorCode(result.error().stream());

                            for (var error : result.error()) {
                                System.err.println(error);
                            }
                        }

                        if (result.isOk() && result.success().isPresent()) {
                            System.out.println(result.success().get());
                        }
                    }
                } else {
                    // Run REPL
                    runRepl();
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

    private static void runRepl() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Interpreter interpreter = new Interpreter(false);

        System.out.print("> ");
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                var result = interpreter.evaluate(line);

                if (result.hasErr()) {
                    for (var error : result.error()) {
                        System.err.println(error);
                    }
                }

                if (result.isOk()) {
                    System.out.println(((Optional) result.success()).get());
                }

                System.out.print("> ");
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }

    private static int determineErrorCode(Stream<Throwable> exceptions) {
        if (exceptions.anyMatch((t) -> t instanceof ParseException)) {
            return ParseException.errorCode();
        } else {
            return EvalException.errorCode();
        }
    }
}
