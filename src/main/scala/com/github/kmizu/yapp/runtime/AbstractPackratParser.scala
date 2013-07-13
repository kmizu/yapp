package com.github.kmizu.yapp.runtime

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.util.Set

abstract class AbstractPackratParser {
  def this(input: String) {
    this()
    `this`(new StringReader(input))
  }

  def this(in: Reader) {
    this()
    this.in = new BufferedReader(in, 20000)
    this.baseIndex = 0
    this.nextIndex = 0
    this.memo_char = new IntSpreadArray
    this.memo_location = new CircularSpreadArray[Location]
    this.lastLine = 1
    this.lastColumn = 1
  }

  def parse: Result[T] = {
    return null
  }

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

  protected final def baseIndex: Int = {
    return baseIndex
  }

  protected final def realIndex(pos: Int): Int = {
    return pos - baseIndex
  }

  protected final def getChar(pos: Int): Int = {
    var i: Int = 0
    var ch: Int = -1
    assert(pos >= baseIndex, "pos must be >= baseIndex")
    if (pos < nextIndex) return memo_char.get(realIndex(pos))
    try { {
      i = nextIndex
      while (i <= pos) {
        {
          ch = in.read
          val realIndex: Int = realIndex(i)
          memo_char.set(realIndex, ch)
          memo_location.set(realIndex, new Location(lastLine, lastColumn))
          if (ch == '\n') {
            lastLine += 1
            lastColumn = 1
          }
          else {
            lastColumn += 1
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    nextIndex = i
    return ch
    }
    catch {
      case e: IOException => {
        throw new RuntimeIOException(e)
      }
    }
  }

  /**
   * ��͑Ώۂ̃f�[�^�̃C���f�b�N�Xpos�ɑΉ�����ʒu��Ԃ��܂��B
   * @param pos �f�[�^�̃C���f�b�N�X
   * @return �\�[�X�R�[�h��̈ʒu
   */
  protected final def getLocation(pos: Int): Location = {
    assert(pos >= baseIndex, "pos must be >= baseIndex")
    return memo_location.get(realIndex(pos))
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def `match`(pos: Int): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def `match`(pos: Int, expected: Char): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual == expected) {
      return new Result[Character](pos + 1, expected)
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def `match`(pos: Int, expected: Set[Character], not: Boolean): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0) {
      val contains: Boolean = expected.contains(actual.asInstanceOf[Char])
      if (contains && !not || (!contains) && not) {
        return new Result[Character](pos + 1, actual.asInstanceOf[Char])
      }
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def matchPositive(pos: Int, expected: Set[Character]): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0 && expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def matchNegative(pos: Int, expected: Set[Character]): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0 && !expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def `match`(pos: Int, expected: CharacterSet, not: Boolean): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0) {
      val contains: Boolean = expected.contains(actual.asInstanceOf[Char])
      if (contains && !not || (!contains) && not) {
        return new Result[Character](pos + 1, actual.asInstanceOf[Char])
      }
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def matchPositive(pos: Int, expected: CharacterSet): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0 && expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def matchNegative(pos: Int, expected: CharacterSet): Result[Character] = {
    val actual: Int = getChar(pos)
    if (actual >= 0 && !expected.contains(actual.asInstanceOf[Char])) {
      return new Result[Character](pos + 1, actual.asInstanceOf[Char])
    }
    return Result.FAIL
  }

  @SuppressWarnings(value = Array("unchecked")) protected final def `match`(pos: Int, str: String): Result[String] = {
    val length: Int = str.length
    {
      var i: Int = 0
      while (i < length) {
        {
          if (getChar(pos + i) != str.charAt(i)) {
            return Result.FAIL
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return new Result[String](pos + length, str)
  }

  protected final def createFailure(pos: Int, message: String): Result[T] = {
    var loc: Location = getLocation(pos)
    if (loc == null) loc = new Location(lastLine, lastColumn)
    return new Result[T](pos, null, new ParseError(loc, message), new Exception)
  }

  private var in: Reader = null
  private var baseIndex: Int = 0
  private var nextIndex: Int = 0
  private var lastLine: Int = 0
  private var lastColumn: Int = 0
  private final val memo_char: IntSpreadArray = null
  private final val memo_location: SpreadArray[Location] = null
}
