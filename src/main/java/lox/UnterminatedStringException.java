package lox;

public class UnterminatedStringException extends ParseException {
  String msg;

  public UnterminatedStringException(String msg, Span span) {
    super("Unterminated string", span);
    this.msg = msg;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + msg;
  }
}
