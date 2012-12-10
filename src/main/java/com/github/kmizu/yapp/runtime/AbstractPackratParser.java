package com.github.kmizu.yapp.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

public abstract class AbstractPackratParser<T> {
  private Reader in;
  private int baseIndex;
  private int nextIndex;
  private int lastLine;
  private int lastColumn;
  private final IntSpreadArray memo_char;
  private final SpreadArray<Location> memo_location;

  public AbstractPackratParser(String input){
    this(new StringReader(input));
  }

  public AbstractPackratParser(Reader in){
    this.in = new BufferedReader(in, 20000);
    this.baseIndex = 0;
    this.nextIndex = 0;
    this.memo_char = new IntSpreadArray();
    this.memo_location = new CircularSpreadArray<Location>();
    this.lastLine = 1;
    this.lastColumn = 1;
  }
  
  public Result<T> parse() {
    return null;
  }

  /**
   * This method discards memory region for memoization.
   * This method should not be called from subclasses.
   * @param toIndex the last index of memory region discarded 
   */
  protected void truncate(int toIndex){
    memo_char.truncate(toIndex);
    memo_location.truncate(toIndex);
  }

  protected final void baseIndex(int newBaseIndex){
    truncate(newBaseIndex - baseIndex);
    this.baseIndex = newBaseIndex;
  }

  protected final int baseIndex(){
    return baseIndex;
  }

  protected final int realIndex(int pos) {
    return pos - baseIndex;
  }

  protected final int getChar(int pos){
    int i, ch = -1;
    assert pos >= baseIndex : "pos must be >= baseIndex";
    if(pos < nextIndex) return memo_char.get(realIndex(pos));
    try {
      for(i = nextIndex; i <= pos; i++){
        ch = in.read();
        int realIndex = realIndex(i);
        memo_char.set(realIndex, ch);
        memo_location.set(realIndex, new Location(lastLine, lastColumn));
        if(ch == '\n'){
          lastLine++;
          lastColumn = 1;
        }else{
          lastColumn++;
        }
      }
      nextIndex = i;
      return ch;
    }catch(IOException e){
      throw new RuntimeIOException(e);
    }
  }

  /**
   * ��͑Ώۂ̃f�[�^�̃C���f�b�N�Xpos�ɑΉ�����ʒu��Ԃ��܂��B
   * @param pos �f�[�^�̃C���f�b�N�X
   * @return �\�[�X�R�[�h��̈ʒu
   */
  protected final Location getLocation(int pos){
    assert pos >= baseIndex : "pos must be >= baseIndex";
    return memo_location.get(realIndex(pos));
  }
  
  @SuppressWarnings(value="unchecked")
  protected final Result<Character> match(int pos){
    int actual = getChar(pos);
    if(actual >= 0){
      return new Result<Character>(pos + 1, (char)actual);
    }
    return Result.FAIL;
  }

  @SuppressWarnings(value="unchecked")
  protected final Result<Character> match(int pos, char expected){
    int actual = getChar(pos);
    if(actual == expected){
      return new Result<Character>(pos + 1, expected);
    }
    return Result.FAIL;
  }

  @SuppressWarnings(value="unchecked")
  protected final Result<Character> match(int pos, Set<Character> expected, boolean not){
    int actual = getChar(pos);
    if(actual >= 0){
      boolean contains = expected.contains((char)actual);
      if(contains && !not || (!contains) && not){
        return new Result<Character>(pos + 1, (char)actual);
      }
    }
    return Result.FAIL;
  }
  
  @SuppressWarnings(value="unchecked")
  protected final Result<Character> matchPositive(int pos, Set<Character> expected){
    int actual = getChar(pos);
    if(actual >= 0 && expected.contains((char)actual)){
      return new Result<Character>(pos + 1, (char)actual);
    }
    return Result.FAIL;
  }
  
  @SuppressWarnings(value="unchecked")
  protected final Result<Character> matchNegative(int pos, Set<Character> expected){
    int actual = getChar(pos);
    if(actual >= 0 && !expected.contains((char)actual)){
      return new Result<Character>(pos + 1, (char)actual);
    }
    return Result.FAIL;
  }
  
  @SuppressWarnings(value="unchecked")
  protected final Result<Character> match(int pos, CharacterSet expected, boolean not){
    int actual = getChar(pos);
    if(actual >= 0){
      boolean contains = expected.contains((char)actual);
      if(contains && !not || (!contains) && not){
        return new Result<Character>(pos + 1, (char)actual);
      }
    }
    return Result.FAIL;
  }
  
  @SuppressWarnings(value="unchecked")
  protected final Result<Character> matchPositive(int pos, CharacterSet expected){
    int actual = getChar(pos);
    if(actual >= 0 && expected.contains((char)actual)) {
      return new Result<Character>(pos + 1, (char)actual);
    }
    return Result.FAIL;
  }
  
  @SuppressWarnings(value="unchecked")
  protected final Result<Character> matchNegative(int pos, CharacterSet expected){
    int actual = getChar(pos);
    if(actual >= 0 && !expected.contains((char)actual)) {
      return new Result<Character>(pos + 1, (char)actual);
    }
    return Result.FAIL;
  }

  @SuppressWarnings(value="unchecked")
  protected final Result<String> match(int pos, String str){
    int length = str.length();
    for(int i = 0; i < length; i++){
      if(getChar(pos + i) != str.charAt(i)){
        return Result.FAIL;
      }
    }
    return new Result<String>(pos + length, str);
  }

  protected final <T> Result<T> createFailure(int pos, String message){
    Location loc = getLocation(pos);
    if(loc == null) loc = new Location(lastLine, lastColumn);
    return new Result<T>(pos, null, new ParseError(loc, message), new Exception());
  }

  /* TODO ������ύX����
  @SuppressWarnings(value="unchecked")
  protected Result<String> match(int pos, Pattern charClass){
    if(pos >= input.length()) return Result.FAIL;
    Matcher matcher = charClass.matcher(input.substring(pos));
    if(!matcher.lookingAt()){
      return Result.FAIL;
    }
    String str = matcher.group();
    return new Result<String>(pos + str.length(), str);
  }
  */
}
