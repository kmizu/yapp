package com.github.kmizu.yapp;

public class SemanticException extends RuntimeException {
  private static final long serialVersionUID = -3752445565800942042L;
  private int line;
  private int column;
  
  public SemanticException(Position pos, String message) {
    super(message);
    this.line   = pos.getLine();
    this.column = pos.getColumn();
  }
  
  public String getErrorMessage() {
    return line + ":" + column + ":" + getMessage();
  }
}
