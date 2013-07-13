package com.github.kmizu.yapp.runtime

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.util.Set

abstract class AbstractPackratParser[T >: Null](input: Reader) {
  private[this] var in: Reader = new BufferedReader(input, 20000)
  private[this] var baseIndex = 0
  private[this] var nextIndex = 0
  private[this] var lastLine = 1
  private[this] var lastColumn = 1
  private[this] val memo_char = new IntSpreadArray
  private[this] val memo_location = new CircularSpreadArray[Location]

  def this(input: String) {
    this(new StringReader(input))
  }

  def parse: Result[T] = null

  /**
   * This method discards memory region for memoization.
   * This method should not be called from subclasses.
   * @param toIndex the last index of memory region discarded
   */
  protected def truncate(toIndex: Int) {
    memo_char.truncate(toIndex)
    memo_location.truncate(toIndex)
  }

  protected final def baseIndex(newBaseIndex: Int) {
    truncate(newBaseIndex - baseIndex)
    this.baseIndex = newBaseIndex
  }

  protected final def realIndex(pos: Int): Int = {
    return pos - baseIndex
  }

  protected final def getChar(pos: Int): Int = {
    require(pos >= baseIndex, "pos must be >= baseIndex")

    if (pos < nextIndex) return memo_char.get(realIndex(pos))

    var i = 0
    var ch = -1
    try {
      i = nextIndex
      while (i <= pos) {
        ch = in.read
        val rIndex: Int = realIndex(i)
        memo_char.set(rIndex, ch)
        memo_location.set(rIndex, new Location(lastLine, lastColumn))
        if (ch == '\n') {
          lastLine += 1
          lastColumn = 1
        } else {
          lastColumn += 1
        }
        i += 1
      }
      nextIndex = i
      ch
    } catch {
      case e: IOException =>  throw new RuntimeIOException(e)
    }
  }

  protected final def getLocation(pos: Int): Location = {
    assert(pos >= baseIndex, "pos must be >= baseIndex")
    memo_location.get(realIndex(pos))
  }

  protected final def `match`(pos: Int): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    Result.FAIL
  }

  protected final def `match`(pos: Int, expected: Char): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual == expected) {
      return new Result[Character](pos + 1, expected)
    }
    Result.FAIL
  }

  protected final def `match`(pos: Int, expected: Set[Character], not: Boolean): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0) {
      val contains: Boolean = expected.contains(actual.asInstanceOf[Char])
      if (contains && !not || (!contains) && not) {
        return new Result[Character](pos + 1, actual.asInstanceOf[Char])
      }
    }
    Result.FAIL
  }

  protected final def matchPositive(pos: Int, expected: Set[Character]): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0 && expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    Result.FAIL
  }

  protected final def matchNegative(pos: Int, expected: Set[Character]): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0 && !expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    Result.FAIL
  }

  protected final def `match`(pos: Int, expected: CharacterSet, not: Boolean): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0) {
      val contains: Boolean = expected.contains(actual.asInstanceOf[Char])
      if (contains && !not || (!contains) && not) {
        return new Result[Character](pos + 1, actual.asInstanceOf[Char])
      }
    }
    Result.FAIL
  }

  protected final def matchPositive(pos: Int, expected: CharacterSet): Result[Character] = {
    val actual = getChar(pos)
    if (actual >= 0 && expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    Result.FAIL
  }

  protected final def matchNegative(pos: Int, expected: CharacterSet): Result[Character] = {
    val actual = getChar(pos)
    if (actual >= 0 && !expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    Result.FAIL
  }

  protected final def `match`(pos: Int, str: String): Result[String] = {
    val length = str.length
    for(i <- 0 until length) {
      if (getChar(pos + i) != str.charAt(i)) {
         return Result.FAIL
      }
    }
    new Result[String](pos + length, str)
  }

  protected final def createFailure(pos: Int, message: String): Result[T] = {
    var loc = getLocation(pos)
    if (loc == null) loc = new Location(lastLine, lastColumn)
    new Result[T](pos, null, new ParseError(loc, message), new Exception)
  }

}
