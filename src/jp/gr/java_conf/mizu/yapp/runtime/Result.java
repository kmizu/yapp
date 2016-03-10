package jp.gr.java_conf.mizu.yapp.runtime;

public class Result<T> {
  private final int pos;
  private final T value;
  private final ParseError error;
  private final Exception debugInfo;
  
  @SuppressWarnings(value="unchecked")
  public final static Result FAIL = new Result(
    -1, null, new ParseError(new Location(0, 0), "default failure object")
  );
  
  public static Result fail() {
    return FAIL;
  }
  
  public Result(int pos, T value){
    this(pos, value, null);
  }
  
  public Result(int pos, T value, ParseError error){
    this(pos, value, error, null);
  }
  
  public Result(int pos, T value, ParseError error, Exception debugInfo){
    this.pos = pos;
    this.value = value;
    this.error = error;
    this.debugInfo = debugInfo;
  }

  
  public int getPos() {
    return pos;
  }
  
  public T getValue() {
    return value;
  }
  
  public ParseError getError() {
    return error;
  }
  
  public boolean isFailure() {
    return error != null;
  }
  
  public boolean isNotFailure() {
    return !isFailure();
  }
  
  public Exception getDebugInfo() {
    return debugInfo;
  }
}
