package com.github.kmizu.yapp.util

import java.io.Closeable
import java.io.IOException

object Closer {
  def close(closable: Closeable): Unit = {
    if (closable != null) try {
      closable.close
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }
  }
}