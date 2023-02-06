logLevel := Level.Warn

addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.1.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

// dependencyUpdates: show a list of project dependencies that can be updated
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.4")

