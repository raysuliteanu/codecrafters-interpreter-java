package lox;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class Util {
  public static boolean matches(Optional<Character> oc, Character c) {
    return matches(oc, new Predicate<Character>() {
      @Override
      public boolean test(Character t) {
        return t == c;
      }

    });
  }

  public static boolean matches(Optional<Character> oc, Predicate<Character> p) {
    return oc.isPresent() && p.test(oc.get());
  }

}
