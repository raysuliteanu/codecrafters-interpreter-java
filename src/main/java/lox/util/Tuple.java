package lox.util;

public interface Tuple {
  Object get(int index);

  int arity();

  Object[] toArray();
}
