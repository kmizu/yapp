organization := "com.github.kmizu"

name := "yapp"

version := "0.2.0"

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.2")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.14" % "test",
  "junit" % "junit" % "4.7" % "test"
)

scalacOptions ++= Seq("-deprecation","-unchecked")

testOptions += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")

seq(sbt.scct.ScctPlugin.instrumentSettings:_*)

initialCommands in console += {
  Iterator().map("import "+).mkString("\n")
}

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
      Some("snapshots" at nexus + "content/repositories/snapshots") 
  else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/kmizu/syapp</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
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
)
