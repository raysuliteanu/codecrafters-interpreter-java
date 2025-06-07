package lox.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class IterablePeekableIteratorTest {

  @Test
  void shouldIterateOverEmptyList() {
    var iterator = new IterablePeekableIterator<>(Collections.emptyList());

    assertThat(iterator.hasNext()).isFalse();
    assertThat(iterator.peek()).isEmpty();
  }

  @Test
  void shouldIterateOverSingleElement() {
    var list = Arrays.asList("hello");
    var iterator = new IterablePeekableIterator<>(list);

    assertThat(iterator.hasNext()).isTrue();
    assertThat(iterator.peek()).isEqualTo(Optional.of("hello"));
    assertThat(iterator.hasNext()).isTrue();
    assertThat(iterator.next()).isEqualTo("hello");
    assertThat(iterator.hasNext()).isFalse();
    assertThat(iterator.peek()).isEmpty();
  }

  @Test
  void shouldIterateOverMultipleElements() {
    var list = Arrays.asList("first", "second", "third");
    var iterator = new IterablePeekableIterator<>(list);

    assertThat(iterator.hasNext()).isTrue();
    assertThat(iterator.peek()).isEqualTo(Optional.of("first"));
    assertThat(iterator.next()).isEqualTo("first");

    assertThat(iterator.hasNext()).isTrue();
    assertThat(iterator.peek()).isEqualTo(Optional.of("second"));
    assertThat(iterator.next()).isEqualTo("second");

    assertThat(iterator.hasNext()).isTrue();
    assertThat(iterator.peek()).isEqualTo(Optional.of("third"));
    assertThat(iterator.next()).isEqualTo("third");

    assertThat(iterator.hasNext()).isFalse();
    assertThat(iterator.peek()).isEmpty();
  }

  @Test
  void shouldPeekMultipleTimesWithoutAdvancing() {
    var list = Arrays.asList(1, 2, 3);
    var iterator = new IterablePeekableIterator<>(list);

    assertThat(iterator.peek()).isEqualTo(Optional.of(1));
    assertThat(iterator.peek()).isEqualTo(Optional.of(1));
    assertThat(iterator.peek()).isEqualTo(Optional.of(1));

    assertThat(iterator.next()).isEqualTo(1);

    assertThat(iterator.peek()).isEqualTo(Optional.of(2));
    assertThat(iterator.peek()).isEqualTo(Optional.of(2));

    assertThat(iterator.next()).isEqualTo(2);
    assertThat(iterator.next()).isEqualTo(3);

    assertThat(iterator.peek()).isEmpty();
  }

  @Test
  void shouldWorkWithNullElements() {
    var list = Arrays.asList("first", null, "third");
    var iterator = new IterablePeekableIterator<>(list);

    assertThat(iterator.next()).isEqualTo("first");
    assertThat(iterator.peek()).isEqualTo(Optional.ofNullable(null));
    assertThat(iterator.next()).isNull();
    assertThat(iterator.next()).isEqualTo("third");
  }

  @Test
  void shouldThrowExceptionWhenCallingNextOnEmptyIterator() {
    var iterator = new IterablePeekableIterator<>(Collections.emptyList());

    assertThatThrownBy(iterator::next)
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldThrowExceptionWhenCallingNextAfterExhaustion() {
    var list = Arrays.asList("only");
    var iterator = new IterablePeekableIterator<>(list);

    iterator.next();

    assertThatThrownBy(iterator::next)
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void shouldReturnSelfAsIterator() {
    var list = Arrays.asList(1, 2, 3);
    var iterator = new IterablePeekableIterator<>(list);

    assertThat(iterator.iterator()).isSameAs(iterator);
  }

  @Test
  void shouldWorkWithEnhancedForLoop() {
    var list = Arrays.asList("a", "b", "c");
    var iterator = new IterablePeekableIterator<>(list);
    var result = new StringBuilder();

    for (String item : iterator) {
      result.append(item);
    }

    assertThat(result.toString()).isEqualTo("abc");
  }

  @Test
  void shouldPeekAfterPartialIteration() {
    var list = Arrays.asList(10, 20, 30, 40);
    var iterator = new IterablePeekableIterator<>(list);

    assertThat(iterator.next()).isEqualTo(10);
    assertThat(iterator.next()).isEqualTo(20);

    assertThat(iterator.peek()).isEqualTo(Optional.of(30));
    assertThat(iterator.hasNext()).isTrue();

    assertThat(iterator.next()).isEqualTo(30);
    assertThat(iterator.peek()).isEqualTo(Optional.of(40));

    assertThat(iterator.next()).isEqualTo(40);
    assertThat(iterator.peek()).isEmpty();
    assertThat(iterator.hasNext()).isFalse();
  }
}
