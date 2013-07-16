package com.github.kmizu.yapp.tools

import java.util.List
import java.util.Map
import com.github.kmizu.yapp.util.CollectionUtil

object CommandLineParser {

  sealed abstract class OptionType
  object OptionType {
    case object INT extends OptionType
    case object STR extends OptionType
    case object NONE extends OptionType
  }

  case class Option(key: String, name: String, `type`: CommandLineParser.OptionType, description: String)
}

class CommandLineParser {
  import CommandLineParser.OptionType
  final val options: Map[String, CommandLineParser.Option] = CollectionUtil.map()
  final val optionValues: Map[String, AnyRef] = CollectionUtil.map()
  final val values: List[String] = CollectionUtil.list()
  final val descriptions: List[String] = CollectionUtil.list()

  def opt(key: String, name: String, `type`: CommandLineParser.OptionType, description: String): CommandLineParser = {
    options.put(name, new CommandLineParser.Option(key, name, `type`, description))
    descriptions.add(description)
    this
  }

  def parse(commandLine: Array[String]) {
    {
      var i: Int = 0
      while (i < commandLine.length) {
        val option: CommandLineParser.Option = options.get(commandLine(i))
        if (i >= commandLine.length - 1)  throw new CommandLineException("no argument is supplied for " + option.name)

        if (option == null) {
          values.add(commandLine(i))
        }else if (option.`type` eq OptionType.NONE) {
          optionValues.put(option.key, new AnyRef)
        } else {
          i += 1
          option.`type` match {
            case OptionType.INT =>
              try {
                optionValues.put(option.key, new java.lang.Integer(Integer.parseInt(commandLine(i))))
              } catch {
                case e: NumberFormatException =>
                  throw new CommandLineException("argument for " + option.name + " must be integer")
              }
            case OptionType.STR =>
              optionValues.put(option.key, commandLine(i))
            case _ =>
              throw new AssertionError("should not reach here")
          }
        }
        i += 1
      }
    }
  }

  def hasOption(key: String): Boolean =  optionValues.containsKey(key)

  def getString(key: String): String = optionValues.get(key).asInstanceOf[String]

  def getString(key: String, defaultValue: String): String = {
    if (optionValues.containsKey(key)) getString(key) else defaultValue
  }

  def getInt(key: String): Int = optionValues.get(key).asInstanceOf[java.lang.Integer].intValue()

  def getInt(key: String, defaultValue: Int): Int = if (optionValues.containsKey(key)) getInt(key) else defaultValue
}
