package com.github.kmizu.yapp.tools

import java.util.List
import java.util.Map

object CommandLineParser {

  final object OptionType {
    final val INT: = null
    final val STR: = null
    final val NONE: = null
  }

  class Option {
    def this(key: String, name: String, `type`: CommandLineParser.OptionType, description: String) {
      this()
      this.key = key
      this.name = name
      this.`type` = `type`
      this.description = description
    }

    final val key: String = null
    final val name: String = null
    final val `type`: CommandLineParser.OptionType = null
    final val description: String = null
  }

}

class CommandLineParser {
  def opt(key: String, name: String, `type`: CommandLineParser.OptionType, description: String): CommandLineParser = {
    options.put(name, new CommandLineParser.Option(key, name, `type`, description))
    descriptions.add(description)
    return this
  }

  def parse(commandLine: Array[String]) {
    {
      var i: Int = 0
      while (i < commandLine.length) {
        {
          val option: CommandLineParser.Option = options.get(commandLine(i))
          if (option == null) {
            values.add(commandLine(i))
            continue //todo: continue is not supported
          }
          if (option.`type` eq OptionType.NONE) {
            optionValues.put(option.key, new AnyRef)
            continue //todo: continue is not supported
          }
          if (i >= commandLine.length - 1) {
            throw new CommandLineException("no argument is supplied for " + option.name)
          }
          i += 1
          option.`type` match {
            case INT =>
              try {
                optionValues.put(option.key, Integer.parseInt(commandLine(i)))
              }
              catch {
                case e: NumberFormatException => {
                  throw new CommandLineException("argument for " + option.name + " must be integer")
                }
              }
              break //todo: break is not supported
            case STR =>
              optionValues.put(option.key, commandLine(i))
              break //todo: break is not supported
            case _ =>
              throw new AssertionError("should not reach here")
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def hasOption(key: String): Boolean = {
    return optionValues.containsKey(key)
  }

  def getString(key: String): String = {
    return optionValues.get(key).asInstanceOf[String]
  }

  def getString(key: String, defaultValue: String): String = {
    return if (optionValues.containsKey(key)) getString(key) else defaultValue
  }

  def getInt(key: String): Int = {
    return optionValues.get(key).asInstanceOf[Integer]
  }

  def getInt(key: String, defaultValue: Int): Int = {
    return if (optionValues.containsKey(key)) getInt(key) else defaultValue
  }

  final val options: Map[String, CommandLineParser.Option] = CollectionUtil.map
  final val optionValues: Map[String, AnyRef] = CollectionUtil.map
  final val values: List[String] = CollectionUtil.list
  final val descriptions: List[String] = CollectionUtil.list
}