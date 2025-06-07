package lox.util;

import java.util.Iterator;
import java.util.Optional;

public class CharSequencePeekableIterator implements PeekableIterator<Character> {

  private final CharSequence in;
  private int offset;

  public CharSequencePeekableIterator(CharSequence in) {
    this.in = in;
  }

  @Override
  public Iterator<Character> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    return offset < in.length();
  }

  @Override
  public Character next() {
    return in.charAt(offset++);
  }

  @Override
  public Optional<Character> peek() {
    return Optional.ofNullable(
        offset < in.length() ? in.charAt(offset) : null);
  }

  @Override
  public String toString() {
    return ("PeekableIterator[next() = " +
        ((offset < in.length()) ? in.charAt(offset) : "null") +
        "]");
  }
}
