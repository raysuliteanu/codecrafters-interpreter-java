package lox.parse;

import lox.LoxException;

public class MissingTokenException extends LoxException {
  // missing token '{}' got '{}'"
  public MissingTokenException(String expected, String actual) {
    super("missing token " + expected + " got " + actual);
  }
}
