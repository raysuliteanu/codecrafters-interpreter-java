package lox;

public abstract class LogUtil {

  public static void log(String msg) {
    System.out.println(msg);
    System.out.flush();
  }

  public static void trace(String msg) {
    System.err.println(msg);
    System.err.flush();
  }

}
