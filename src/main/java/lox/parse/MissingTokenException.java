package lox.parse;

import lox.LoxException;

public class MissingTokenException extends LoxException {
  public MissingTokenException(String expected, String actual) {
    super("missing token " + expected + " got " + actual);
  }
}
