package lox.util;

public abstract class LogUtil {

    private static final String TRACE = "TRACE_ENABLED";

    public static void log(String msg) {
        System.out.println(msg);
        System.out.flush();
    }

    public static void log(Throwable t) {
        System.err.println(t);
        System.err.flush();
    }

    public static void trace(String msg) {
        if (System.getenv().containsKey(TRACE)) {
            System.err.println(msg);
            System.err.flush();
        }
    }

    public static void trace(Throwable t) {
        if (System.getenv().containsKey(TRACE)) {
            System.err.println(t);
            System.err.flush();
        }
    }
}
