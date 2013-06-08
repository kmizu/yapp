package com.github.kmizu.yapp.util

import java.io.File

object SystemProperties {
  final val LINE_SEPARATOR: String = System.getProperty("line.separator")
  final val PATH_SEPARATOR: String = File.pathSeparator
  final val FILE_SEPARATOR: String = File.separator
}