package com.github.kmizu.yapp.tools

import java.io.File
import java.io.IOException

object YappAllInDirMain {
  def main(args: Array[String]) {
    val srcDir = new File(args(0))
    val dstDir = new File(args(1))
    for (file <- srcDir.listFiles) {
      if (file.getName.endsWith(".ypp")) {
        val newArgs: Array[String] = new Array[String](args.length)
        System.arraycopy(args, 2, newArgs, 0, args.length - 2)
        newArgs(newArgs.length - 2) = file.getCanonicalPath
        newArgs(newArgs.length - 1) = dstDir.getCanonicalPath
        new YappMain(newArgs:_*).generate
      }
    }
  }
}
