val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "simulation",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    // Augmenter la mémoire max pour l'exécution (utile pour `run`)
    run / javaOptions += "-Xmx4G",

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "com.lihaoyi" %% "ujson" % "3.1.0",

    libraryDependencies += "org.http4s" %% "http4s-dsl" % "0.23.24",
    libraryDependencies += "org.http4s" %% "http4s-ember-server" % "0.23.24",
    libraryDependencies += "org.http4s" %% "http4s-circe" % "0.23.24",

    libraryDependencies += "io.circe" %% "circe-generic" % "0.14.6",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.2",

    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
  )
