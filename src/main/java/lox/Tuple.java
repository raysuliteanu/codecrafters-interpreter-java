package lox;

interface Tuple {
  Object get(int index);

  int arity();

  Object[] toArray();
}
