val scala3Version = "3.7.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "simulation",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "com.lihaoyi" %% "ujson" % "3.1.0"
  )
