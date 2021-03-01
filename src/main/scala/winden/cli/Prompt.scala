package io.github.mkotsur
package winden.cli

import cats.Show
import cats.effect.IO

import scala.io.StdIn
import scala.util.Try
import cats.implicits._

case class Prompt[A: Show](question: String, parseAnswer: String => A, defaultAnswer: A) {
  def toIO: IO[A] =
    for {
      strNoSpaces <- IO(
        StdIn
          .readLine(s"$question [enter for ${defaultAnswer.show}] >")
          .replaceAll(" ", "")
      )
      res <- strNoSpaces match {
        case ""    => defaultAnswer.pure[IO]
        case other => IO.fromTry(Try(parseAnswer(other)))
      }
    } yield res
}
