package com.github.kmizu.yapp.tools

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.util.HashSet
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set
import com.github.kmizu.yapp.Ast
import com.github.kmizu.yapp.CompilationException
import com.github.kmizu.yapp.Pair
import com.github.kmizu.yapp.SemanticException
import com.github.kmizu.yapp.Symbol
import com.github.kmizu.yapp.Ast.Grammar
import com.github.kmizu.yapp.parser.ParseException
import com.github.kmizu.yapp.parser.Token
import com.github.kmizu.yapp.parser.YappParser
import com.github.kmizu.yapp.tools.CommandLineParser.OptionType
import com.github.kmizu.yapp.tr.AstPrinter
import com.github.kmizu.yapp.tr.AutoCutInserter
import com.github.kmizu.yapp.tr.AutoCutInserterUsingFirstSet
import com.github.kmizu.yapp.tr.AutoCutInserterUsingFollowSet
import com.github.kmizu.yapp.tr.BoundedQualifierCounter
import com.github.kmizu.yapp.tr.CodeGeneratorFromIst
import com.github.kmizu.yapp.tr.ComposableTranslator
import com.github.kmizu.yapp.tr.CutCounter
import com.github.kmizu.yapp.tr.DeadRuleEliminator
import com.github.kmizu.yapp.tr.DebugPrinterForFirstSet
import com.github.kmizu.yapp.tr.GrammarToIstTranslator
import com.github.kmizu.yapp.tr.IdentityTranslator
import com.github.kmizu.yapp.tr.MacroExpander
import com.github.kmizu.yapp.tr.MacroExpanderEx
import com.github.kmizu.yapp.tr.MemoizedCountPrinter
import com.github.kmizu.yapp.tr.NeedlessMemoDetector
import com.github.kmizu.yapp.tr.Parser
import com.github.kmizu.yapp.tr.RuleCounter
import com.github.kmizu.yapp.tr.RuleInliner
import com.github.kmizu.yapp.tr.SpaceComplexityChecker
import com.github.kmizu.yapp.tr.SyntaxSugarExpander
import com.github.kmizu.yapp.tr.Translator
import com.github.kmizu.yapp.tr.UnboundedContext
import com.github.kmizu.yapp.tr.VoidTranslator
import com.github.kmizu.yapp.util.SystemProperties._

object YappMain {
  def main(args: Array[String]) {
    try {
      new YappMain(args).generate
    }
    catch {
      case e: CommandLineException => {
        System.err.println(e.getMessage)
      }
    }
  }

  private def removeDuplicated[T](list: List[T]): Unit = {
    val set: Set[T] = new HashSet[T]
    val it: Iterator[T] = list.iterator
    while (it.hasNext) {
      val t: T = it.next
      if (set.contains(t)) it.remove
      set.add(t)
    }
  }
}

class YappMain(args: String*) {
  private final val USAGE = {
    val usage = new StringBuilder
    usage.append("Usage: java com.github.kmizu.yapp.tools.YappMain [options ...] <file name> [<dest dir>]" + LINE_SEPARATOR).append("options:" + LINE_SEPARATOR)
    import scala.collection.JavaConversions._
    for (desc <- parser.descriptions) {
      usage.append("  " + desc + LINE_SEPARATOR)
    }
    new String(usage.toString())
  }
  private var srcFile: File = null
  private var dstDir: File = null
  private[tools] var parser = new CommandLineParser

  parser.opt("pre", "--pre", OptionType.STR, "--pre        specify parser class' prefix").opt("time", "--time", OptionType.NONE, "--time       measure time elapsed").opt("pkg", "--pkg", OptionType.STR, "--pkg        specify parser class' package").opt("pm", "--pm", OptionType.NONE, "--pm         print the number of rules that should be memoized").opt("edr", "--edr", OptionType.NONE, "--edr        eliminate dead rules").opt("em", "--em", OptionType.NONE, "--em        eliminate needless memoizations").opt("reg", "--reg", OptionType.NONE, "--reg        insert cut automatically by regex like method").opt("Onj", "--Onj", OptionType.NONE, "--Onj        turn off 'join column optimization'").opt("ac", "--ac", OptionType.NONE, "--ac         turn on 'auto cut insertion' optimization").opt("inl", "--inl", OptionType.INT, "--inl        inline nonterminal expression").opt("ACfirst", "--ACfirst", OptionType.INT, "--ACfirst    auto cut insertion by first set method").opt("ACfollow", "--ACfollow", OptionType.NONE, "--ACfollow   auto cut insertion by follow set method").opt("space", "--space", OptionType.NONE, "--space      calculate space complexity of specified rule")
  parser.parse(args.toArray)
  if (parser.values.size == 0) {
    throw new CommandLineException(USAGE)
  }
  srcFile = new File(parser.values.get(0))
  dstDir = if (parser.values.size > 1) new File(parser.values.get(1)) else new File(".")

  def generate: Boolean = {
    try {
      generateMain
      true
    } catch {
      case ex: CompilationException =>
        if (ex.getReason.isInstanceOf[ParseException]) {
          val e = ex.getReason.asInstanceOf[ParseException]
          val error = e.currentToken.next
          val expected: String = e.tokenImage(e.expectedTokenSequences(0)(0))
          System.err.printf("%d:%d: expected %s, but %s.%n", new java.lang.Integer(error.beginLine), new java.lang.Integer(error.beginColumn), expected, error)
        }
        else if (ex.getReason.isInstanceOf[SemanticException]) {
          val e: SemanticException = ex.getReason.asInstanceOf[SemanticException]
          System.err.println(e.getErrorMessage)
        } else {
          ex.printStackTrace
        }
        false
    }
  }

  private def generateMain {
    var start: Long = -1
    if (has("time")) {
      start = System.currentTimeMillis
    }
    if (has("ac")) {
      val compiler: Translator[File, Void] = ComposableTranslator.empty[File].compose(new Parser(getString("pre", ""))).compose(new MacroExpander).compose(new SyntaxSugarExpander).compose(if (has("ACfirst")) new AutoCutInserterUsingFirstSet(getInt("ACfirst")) else new IdentityTranslator[Ast.Grammar]).compose(new CutCounter).compose(if (has("ACfollow")) new AutoCutInserterUsingFollowSet else new IdentityTranslator[Ast.Grammar]).compose(new CutCounter).compose(new AstPrinter).compose(new GrammarToIstTranslator(false)).compose(new CodeGeneratorFromIst(if (has("pkg")) getString("pkg") else null, dstDir, !has("Onj")))
      compiler.translate(srcFile)
    }
    else if (has("em")) {
      val compiler: Translator[File, Void] = ComposableTranslator.empty[File].compose(new Parser(getString("pre", ""))).compose(new MacroExpander).compose(new SyntaxSugarExpander).compose(new NeedlessMemoDetector).compose(if (has("pm")) new MemoizedCountPrinter else new VoidTranslator[Pair[Ast.Grammar, Map[Symbol, Boolean]]])
      compiler.translate(srcFile)
    }
    else if (has("reg")) {
      val inlineLimit: Int = if (has("inl")) getInt("inl") else 1
      val compiler: Translator[File, Void] = ComposableTranslator.empty[File].compose(new Parser(getString("pre", ""))).compose(new MacroExpander).compose(new SyntaxSugarExpander).compose(if (inlineLimit > 0) new RuleInliner(inlineLimit) else new IdentityTranslator[Ast.Grammar]).compose(new AutoCutInserter).compose(if (has("edr")) new DeadRuleEliminator else new IdentityTranslator[Ast.Grammar]).compose(new CutCounter).compose(new AstPrinter).compose(new VoidTranslator[Ast.Grammar])
      compiler.translate(srcFile)
    }
    else if (has("space")) {
      val compiler: Translator[File, List[UnboundedContext]] = ComposableTranslator.empty[File].compose(new Parser(getString("pre", ""))).compose(new MacroExpanderEx).compose(new RuleCounter).compose(new BoundedQualifierCounter).compose(new SpaceComplexityChecker)
      val unboundedExpressions: List[UnboundedContext] = compiler.translate(srcFile)
      if (unboundedExpressions.size == 0) {
        printf("A parser generated from this grammar requires only bounded space.%n")
      }
      else {
        printf("A parser generated from this grammar may require unbounded space%n")
        printf("because the following expressions are unbounded:%n")
        import scala.collection.JavaConversions._
        for (c <- unboundedExpressions) {
          printf("%s in %s%n", c.unboundedExpression, c.parent)
        }
      }
    }
    else {
      val compiler: Translator[File, Void] = ComposableTranslator.empty[File].compose(new Parser(getString("pre", ""))).compose(new MacroExpander).compose(new SyntaxSugarExpander).compose(new RuleCounter).compose(new GrammarToIstTranslator(has("em"))).compose(new CodeGeneratorFromIst(if (has("pkg")) getString("pkg") else null, dstDir, !has("onj")))
      compiler.translate(srcFile)
    }
    if (has("time")) {
      printf("time: %d[ms]%n", System.currentTimeMillis - start)
    }
  }

  private def has(key: String): Boolean = {
    return parser.hasOption(key)
  }

  private def getInt(key: String): Int = {
    return parser.getInt(key)
  }

  private def getInt(key: String, defaultValue: Int): Int = {
    return parser.getInt(key, defaultValue)
  }

  private def getString(key: String): String = {
    return parser.getString(key)
  }

  private def getString(key: String, defaultValue: String): String = {
    return parser.getString(key, defaultValue)
  }

}