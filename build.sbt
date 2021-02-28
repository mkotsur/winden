name := "winden"

version := "0.1"

scalaVersion := "2.13.4"

idePackagePrefix := Some("io.github.mkotsur")

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.3.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"

libraryDependencies += "org.mockito" %% "mockito-scala" % "1.10.0" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % "test"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1" % "test"
