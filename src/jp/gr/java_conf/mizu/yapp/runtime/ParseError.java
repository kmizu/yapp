package jp.gr.java_conf.mizu.yapp.runtime;

import java.util.Formatter;

public class ParseError {
  private final Location location;
  private final String message;
  
  public ParseError(Location location, String message) {
    this.location = location;
    this.message = message;
  }
  
  public Location getLocation() {
    return location;
  }
  
  public int getLine() {
    return location.getLine();
  }
  
  public int getColumn() {
    return location.getColumn();
  }
  
  public String getMessage() {
    return message;
  }
  
  public String getErrorMessage() {
    StringBuilder builder = new StringBuilder();
    Formatter f = new Formatter(builder);
    f.format("%d, %d: %s", location.getLine(), location.getColumn(), message);
    f.flush();
    return new String(builder);
  }
}
