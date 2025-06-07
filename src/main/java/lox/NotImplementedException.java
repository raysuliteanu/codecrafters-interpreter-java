package lox;

public class NotImplementedException extends LoxException {
  public NotImplementedException(String msg) {
    super("not implemented: " + msg);
  }
}
