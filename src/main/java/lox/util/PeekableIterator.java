package lox.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

public interface PeekableIterator<T> extends Iterator<T>, Iterable<T> {
  Optional<T> peek();

  default Optional<T> nextIf(Predicate<T> p) {
    if (peek().isPresent() && p.test(peek().get())) {
      return Optional.of(next());
    }

    return Optional.<T>empty();

  }
}
