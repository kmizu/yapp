import sbt._
import Keys._
import classpath._
import java.util.jar.{Attributes, Manifest}


def javacc(classpath: Classpath, output: File, logger: Logger): Seq[File] = {
  Fork.java(
    ForkOptions(outputStrategy = Some(LoggedOutput(logger))),
    "-cp" ::
    Path.makeString(classpath.map(_.data)) ::
    List(
      "javacc",
      "-UNICODE_INPUT=true",
      "-JAVA_UNICODE_ESCAPE=true",
      "-OUTPUT_DIRECTORY=%s/com/github/kmizu/yapp/parser".format(output.toString),
      "grammar/YappParser.jj"
    )
  ) match {
    case exitCode if exitCode != 0 => sys.error("Nonzero exit code returned from javacc: " + exitCode)
    case 0 =>
  }
  (output ** "*.java").get
}

def yapp(classpath: Classpath, options: List[String], input: File, output: File, logger: Logger): Seq[File] = {
  Fork.java(
    ForkOptions(outputStrategy = Some(LoggedOutput(logger))),
    "-cp" :: Path.makeString(classpath.map(_.data)) ::
      "com.github.kmizu.yapp.tools.YappMain" ::
      (options ::: List(input.toString, output.toString))
  ) match {
    case exitCode if exitCode != 0 => sys.error("Nonzero exit code returned from javacc: " + exitCode)
    case 0 =>
  }
  (output ** "*.java").get
}


val scaladocBranch = settingKey[String]("branch name for scaladoc -doc-source-url")

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.1",
  organization := "com.github.kmizu"
)

lazy val rootSettings = Seq(
  name := "yapp",
  libraryDependencies ++= Seq(
    "net.java.dev.javacc" % "javacc" % "5.0",
    "junit" % "junit" % "4.7" % "test",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  ),
  sourceGenerators in Compile += Def.task {
    val cp = (externalDependencyClasspath in Compile).value
    val dir = (sourceManaged in Compile).value
    val s = streams.value
    val parser = dir / "java" / "com" / "github" / "kmizu" / "yapp" / "parser" / "YappParser.java"
    val grammar = new java.io.File("grammar") / "YappParser.jj"
    val grammarLastModified = grammar.lastModified
    val parserLastModified = parser.lastModified
    if(grammarLastModified > parserLastModified) {
     javacc(cp, dir / "java", s.log)
    } else {
      (dir / "java" ** "**.java").get
    }

  }.taskValue,
  publishMavenStyle := true,
  scaladocBranch := "master",
  scalacOptions in (Compile, doc) ++= { Seq(
    "-sourcepath", baseDirectory.value.getAbsolutePath,
    "-doc-source-url", s"https://github.com/kmizu/yapp/tree/${scaladocBranch.value}€{FILE_PATH}.scala"
  )},
  pomExtra := (
    <url>https://github.com/kmizu/yapp</url>
    <licenses>
      <license>
        <name>The MIT License</name>
        <url>http://www.opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:kmizu/yapp.git</url>
      <connection>scm:git:git@github.com:kmizu/yapp.git</connection>
    </scm>
    <developers>
      <developer>
        <id>kmizu</id>
        <name>Kota Mizushima</name>
        <url>https://github.com/kmizu</url>
      </developer>
    </developers>
  ),
  initialCommands in console += {
    Iterator(
      "com.github.kmizu.yapp._"
    ).map("import "+).mkString("\n")
  },
  publishTo := {
    val v = version.value
    val nexus = "https://oss.sonatype.org/"
    if (v.endsWith("-SNAPSHOT"))
      Some("snapshots" at nexus+"content/repositories/snapshots")
    else
      Some("releases" at nexus+"service/local/staging/deploy/maven2")
  },
  credentials ++= {
    val sonatype = ("Sonatype Nexus Repository Manager", "oss.sonatype.org")
    def loadMavenCredentials(file: java.io.File) : Seq[Credentials] = {
      xml.XML.loadFile(file) \ "servers" \ "server" map (s => {
        val host = (s \ "id").text
        val realm = if (host == sonatype._2) sonatype._1 else "Unknown"
        Credentials(realm, host, (s \ "username").text, (s \ "password").text)
      })
    }
    val ivyCredentials   = Path.userHome / ".ivy2" / ".credentials"
    val mavenCredentials = Path.userHome / ".m2"   / "settings.xml"
    (ivyCredentials.asFile, mavenCredentials.asFile) match {
      case (ivy, _) if ivy.canRead => Credentials(ivy) :: Nil
      case (_, mvn) if mvn.canRead => loadMavenCredentials(mvn)
      case _ => Nil
    }
  }
)

lazy val benchmarkSettings = Seq(
  name := "yapp_benchmark",
  sourceGenerators in Compile += Def.task {
    val cp = (dependencyClasspath in Compile).value
    val dir = (sourceManaged in Compile).value
    val s = streams.value
    val parser = dir / "com" / "github" / "kmizu" / "yapp" / "benchmark" / "parser"
    val packageName = "com.github.kmizu.yapp.benchmark.parser"
    val input = baseDirectory.value / "grammar"
    parser.mkdirs()
    var files: Seq[File] = Seq.empty
    files ++= yapp(cp, List("--package", packageName), input / "JavaRecognizer.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName, "--pre", "AC", "--ac", "--ACfirst", "-1", "--ACfollow"), input / "JavaRecognizer.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "OptimizedJavaRecognizer.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "XMLParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName, "--pre", "AC", "--ac", "--ACfirst", "-1", "--ACfollow"), input / "XMLParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "OptimizedXMLParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "JSONParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName, "--pre", "AC", "--ac", "--ACfirst", "-1", "--ACfollow"), input / "JSONParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "OptimizedJSONParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "ESLLParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName, "--pre", "AC", "--ac", "--ACfirst", "-1", "--ACfollow"), input / "ESLLParser.ypp", parser, s.log)
    files ++= yapp(cp, List("--package", packageName), input / "OptimizedESLLParser.ypp", parser, s.log)
    files
  }.taskValue
)

lazy val root = Project(
  id = "root",
  base = file(".")
).settings(commonSettings).settings(rootSettings)

lazy val benchmark = Project(
  id = "benchmark",
  base = file("benchmark")
).settings(commonSettings).dependsOn(root).settings(benchmarkSettings)
