package lox.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public interface PeekableIterator<T> extends Iterator<T>, Iterable<T> {
  /**
   * An iterator with a one-element peek-ahead.
   * Peek one value ahead, if present. If peek() is called multiplle times before
   * a call to next(), the same value is always returned.
   *
   * @returns Optional.of(T) if there is another element, or Optional.empty()
   */
  Optional<T> peek();

  default Optional<T> nextIf(Predicate<T> p) {
    if (peek().isPresent() && p.test(peek().get())) {
      return Optional.of(next());
    }

    return Optional.<T>empty();

  }
}
