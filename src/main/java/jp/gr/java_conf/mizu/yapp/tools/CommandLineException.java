package jp.gr.java_conf.mizu.yapp.tools;

public class CommandLineException extends RuntimeException {
  private static final long serialVersionUID = -4221400901082236315L;

  public CommandLineException(String message) {
    super(message);
  }
}
