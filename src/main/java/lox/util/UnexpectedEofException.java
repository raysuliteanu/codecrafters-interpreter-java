package lox.util;

import lox.LoxException;

public class UnexpectedEofException extends LoxException {
  public UnexpectedEofException() {
    super("Unexpected EOF");
  }

}
