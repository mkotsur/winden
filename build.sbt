ThisBuild / name := "winden"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.13.4"

ThisBuild / idePackagePrefix := Some("io.github.mkotsur")

lazy val core = project.settings(
  libraryDependencies ++= deps.testAll ++ Seq(
    deps.catsEffect,
    deps.betterFiles,
    deps.circe,
    deps.circeParser
  ),
  settings.CompilerPlugins
//  settings.SimplePaths
)

lazy val web = project
  .settings(
    libraryDependencies ++= deps.testAll ++ Seq(
      deps.catsEffect,
      deps.betterFiles
    ) ++ deps.http4sAll,
    settings.CompilerPlugins
//  settings.SimplePaths
  )
  .dependsOn(core)

lazy val cli = project
  .settings(
    libraryDependencies ++= deps.testAll ++ Seq(deps.catsEffect, deps.betterFiles),
    settings.CompilerPlugins
//  settings.SimplePaths
  )
  .dependsOn(core)

lazy val deps = new {
  private lazy val V = new {
    val catsEffect = "2.3.1"
    val pureconfig = "0.12.2"
    val http4s     = "0.21.20"
    val circe      = "0.13.0"
  }

  val betterFiles    = "com.github.pathikrit"  %% "better-files"           % "3.9.1"
  val catsEffect     = "org.typelevel"         %% "cats-effect"            % V.catsEffect
  val pureconfig     = "com.github.pureconfig" %% "pureconfig"             % V.pureconfig
  val pureconfigCats = "com.github.pureconfig" %% "pureconfig-cats-effect" % V.pureconfig
  val circe          = "io.circe"              %% "circe-generic"          % V.circe
  val circeParser    = "io.circe"              %% "circe-parser"           % V.circe
  val http4sAll = Seq(
    "org.http4s" %% "http4s-dsl"          % V.http4s,
    "org.http4s" %% "http4s-blaze-server" % V.http4s,
    "org.http4s" %% "http4s-blaze-client" % V.http4s,
    "org.http4s" %% "http4s-circe"        % V.http4s,
    circe
  )

  val testAll = Seq(
    "org.scalatest" %% "scalatest"       % "3.1.0"  % "test",
    "org.mockito"   %% "mockito-scala"   % "1.10.0" % "test",
    "ch.qos.logback" % "logback-classic" % "1.2.3"  % "test"
  )
}

lazy val settings = new {

//  val SimplePaths = Seq(
//    Compile / scalaSource := baseDirectory.value / "src",
//    Compile / resourceDirectory := baseDirectory.value / "res"
//  )

  val CompilerPlugins = Seq(
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full)
  )
}
