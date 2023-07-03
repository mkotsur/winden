Global / excludeLintKeys += ThisBuild / idePackagePrefix
Global / excludeLintKeys += ThisBuild / name

ThisBuild / name := "winden"

ThisBuild / version := "0.1"

ThisBuild / idePackagePrefix := Some("io.github.mkotsur")

inThisBuild(
  List(
    scalaVersion := "2.13.11",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := "2.13"
  )
)

ThisBuild / scalacOptions ++= Seq(
  "-Xlint:unused"
)

lazy val core = project.settings(
  libraryDependencies ++= deps.testAll ++ Seq(
    deps.catsEffect,
    deps.betterFiles,
    deps.circe,
    deps.circeParser,
    deps.pureConfig
  ) ++ deps.loggingAll,
  settings.CompilerPlugins
)

lazy val web = project
  .settings(
    libraryDependencies ++= deps.testAll ++ Seq(
      deps.catsEffect,
      deps.betterFiles
    ) ++ deps.http4sAll,
    settings.CompilerPlugins
  )
  .dependsOn(core)

lazy val cli = project
  .settings(
    libraryDependencies ++= deps.testAll ++ Seq(deps.catsEffect, deps.betterFiles),
    settings.CompilerPlugins
  )
  .dependsOn(core)

lazy val deps = new {
  private lazy val V = new {
    val catsEffect = "3.5.1"
    val pureconfig = "0.12.2"
    val http4s     = "0.23.15"
    val http4sDsl  = "0.23.22"
    val log4cats   = "2.6.0"
    val circe      = "0.14.5"
  }

  val betterFiles    = "com.github.pathikrit"  %% "better-files"           % "3.9.2"
  val catsEffect     = "org.typelevel"         %% "cats-effect"            % V.catsEffect
  val pureconfig     = "com.github.pureconfig" %% "pureconfig"             % V.pureconfig
  val pureconfigCats = "com.github.pureconfig" %% "pureconfig-cats-effect" % V.pureconfig
  val circe          = "io.circe"              %% "circe-generic"          % V.circe
  val circeParser    = "io.circe"              %% "circe-parser"           % V.circe
  val http4sAll = Seq(
    "org.http4s" %% "http4s-dsl"          % V.http4sDsl,
    "org.http4s" %% "http4s-circe"        % V.http4sDsl,
    "org.http4s" %% "http4s-blaze-server" % V.http4s,
    "org.http4s" %% "http4s-blaze-client" % V.http4s,
    circe
  )

  val loggingAll = Seq(
    "org.typelevel" %% "log4cats-slf4j"  % V.log4cats,
    "ch.qos.logback" % "logback-classic" % "1.4.8"
  )

  val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.17.4"

  val testAll = Seq(
    "org.scalatest" %% "scalatest"     % "3.2.16"  % "test",
    "org.mockito"   %% "mockito-scala" % "1.17.14" % "test"
  )
}

lazy val settings = new {

  val CompilerPlugins = Seq(
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)
  )
}
