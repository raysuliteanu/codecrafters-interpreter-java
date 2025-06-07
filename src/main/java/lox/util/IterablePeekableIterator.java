package lox.util;

import java.util.Iterator;
import java.util.Optional;

public class IterablePeekableIterator<T> implements PeekableIterator<T> {

  private final Iterator<T> iterator;
  private Optional<T> peeked = Optional.empty();
  private boolean hasPeeked = false;

  public IterablePeekableIterator(Iterable<T> iterable) {
    this.iterator = iterable.iterator();
  }

  @Override
  public Iterator<T> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    return hasPeeked || iterator.hasNext();
  }

  @Override
  public T next() {
    if (hasPeeked) {
      hasPeeked = false;
      return peeked.orElse(null);
    }
    return iterator.next();
  }

  @Override
  public Optional<T> peek() {
    if (!hasPeeked && iterator.hasNext()) {
      peeked = Optional.ofNullable(iterator.next());
      hasPeeked = true;
    }
    return hasPeeked ? peeked : Optional.empty();
  }
}
