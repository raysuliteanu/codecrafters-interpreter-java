package lox.util;


public abstract class Tuples {
  public static Tuple of(Object one, Object two) {
    return new Tuple() {
      @Override
      public Object get(int index) {
        if (index == 0) {
          return one;
        } else if (index == 1) {
          return two;
        } else {
          throw new RuntimeException("invalid tuple access: " + index);
        }
      }

      @Override
      public int arity() {
        return 2;
      }

      @Override
      public Object[] toArray() {
        return new Object[] { one, two };
      }

      @Override
      public String toString() {
        return "Tuple[" + one + ", " + two + "]";
      }
    };
  }
}
