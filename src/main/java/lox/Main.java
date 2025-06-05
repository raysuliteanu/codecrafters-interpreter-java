package lox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("Usage: ./your_program.sh <command> <filename>");
      System.err.println("Commands: tokenize, parse, evaluate");
      System.exit(1);
    }

    String command = args[0];
    String filename = args[1];

    if (!command.equals("tokenize") && !command.equals("parse") && !command.equals("evaluate")) {
      System.err.println("Unknown command: " + command);
      System.exit(1);
    }

    String fileContents = "";
    try {
      fileContents = Files.readString(Path.of(filename));
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      System.exit(1);
    }

    int rc = 0;
    if (fileContents.length() > 0) {
      switch (command) {
        case "tokenize":
          Result<List<Token>, List<Throwable>> result = new Scanner().scan(fileContents);

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
          System.out.println("EOF  null");
          break;
        case "parse":
          System.err.println("Parse command not yet implemented");
          rc = 1;
          break;
        case "evaluate":
          System.err.println("Evaluate command not yet implemented");
          rc = 1;
          break;
      }
    }

    System.exit(rc);
  }
}
