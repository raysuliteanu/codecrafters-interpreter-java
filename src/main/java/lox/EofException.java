
package lox;

import java.util.Objects;

public class EofException extends ParseException {
  public EofException(Span span) {
    this("Unexpected EOF", span);
  }

  public EofException(String message, Span span) {
    super(message, span);
  }
}
