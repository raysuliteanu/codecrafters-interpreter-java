package lox;

import java.util.Collection;

public record Result<T, E>(T success, E error) {
  public boolean hasErr() {
    if (error instanceof Collection<?> e) {
      return !e.isEmpty();
    } else {
      return error != null;
    }
  }

  public boolean isOk() {
    if (success instanceof Collection<?> s) {
      return !s.isEmpty();
    } else {
      return success != null;
    }
  }
}
