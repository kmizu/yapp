package jp.gr.java_conf.mizu.yapp;

public class CompilationException extends RuntimeException {
  public CompilationException(Exception reason) {
    super(reason);
  }
  
  public Throwable getReason() {
    return getCause();
  }
}
