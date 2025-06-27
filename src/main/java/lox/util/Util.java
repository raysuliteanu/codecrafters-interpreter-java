package lox.util;

import java.util.Optional;
import java.util.function.Predicate;

import lox.eval.BooleanResult;

public abstract class Util {
    public static boolean matches(Optional<Character> oc, Character c) {
        return matches(oc, (t) -> t == c);
    }

    public static boolean matches(Optional<Character> oc, Predicate<Character> p) {
        return oc.isPresent() && p.test(oc.get());
    }

    public static boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Boolean b) {
            return b;
        }

        if (object instanceof BooleanResult b) {
            return b.value();
        }

        return true;
    }
}
