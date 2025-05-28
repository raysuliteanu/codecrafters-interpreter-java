package lox;

public record Span(long line, SourceSpan sourceSpan) {
  public static Span of(long line, long offset, long length) {
    var ss = new SourceSpan(offset, length);
    return new Span(line, ss);
  }
}
