// Your sbt build file. Guides on how to write one can be found at
// http://www.scala-sbt.org/0.13/docs/index.html

val sparkVer = sys.props.getOrElse("spark.version", "2.1.0")
val sparkBranch = sparkVer.substring(0, 3)
val defaultScalaVer = sparkBranch match {
  case "2.0" => "2.11.8"
  case "2.1" => "2.11.8"
  case "2.2" => "2.11.8"
  case _ => throw new IllegalArgumentException(s"Unsupported Spark version: $sparkVer.")
}
val scalaVer = sys.props.getOrElse("scala.version", defaultScalaVer)
val scalaMajorVersion = scalaVer.substring(0, scalaVer.indexOf(".", scalaVer.indexOf(".") + 1))
val defaultScalaTestVer = scalaVer match {
  case s if s.startsWith("2.10") => "2.0"
  case s if s.startsWith("2.11") => "2.2.6" // scalatest_2.11 does not have 2.0 published
}

sparkVersion := sparkVer

scalaVersion := scalaVer

spName := "spark-deep-learning/spark-deep-learning"

// Don't forget to set the version
version := s"0.1.0-spark$sparkBranch"

// All Spark Packages need a license
licenses := Seq("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

spAppendScalaVersion := true

// Add Spark components this package depends on, e.g, "mllib", ....
sparkComponents ++= Seq("mllib-local", "mllib", "sql")

// uncomment and change the value below to change the directory where your zip artifact will be created
// spDistDirectory := target.value

// add any Spark Package dependencies using spDependencies.
// e.g. spDependencies += "databricks/spark-avro:0.1"
spDependencies += s"databricks/tensorframes:0.2.8-s_${scalaMajorVersion}"

libraryDependencies += "org.scalatest" %% "scalatest" % defaultScalaTestVer % "test"

// These versions are ancient, but they cross-compile around scala 2.10 and 2.11.
// Update them when dropping support for scala 2.10
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-api" % "2.1.2"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"

assemblyMergeStrategy in assembly := {
  case "requirements.txt" => MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

parallelExecution := false

scalacOptions in (Compile, doc) ++= Seq(
  "-groups",
  "-implicits",
  "-skip-packages", Seq("org.apache.spark").mkString(":"))

scalacOptions in (Test, doc) ++= Seq("-groups", "-implicits")

// This fixes a class loader problem with scala.Tuple2 class, scala-2.11, Spark 2.x
fork in Test := true

// This and the next line fix a problem with forked run: https://github.com/scalatest/scalatest/issues/770
javaOptions in Test ++= Seq("-Xmx2048m", "-XX:ReservedCodeCacheSize=384m", "-XX:MaxPermSize=384m")

concurrentRestrictions in Global := Seq(
  Tags.limitAll(1))

autoAPIMappings := true

coverageHighlighting := false
