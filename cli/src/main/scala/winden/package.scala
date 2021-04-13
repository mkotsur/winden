package io.github.mkotsur
import winden.model.Prompt

import cats.Show
import cats.effect.IO
import cats.implicits._

import scala.io.StdIn
import scala.util.Try

package object winden {

  object implicits {
    implicit class PromptEffectIO[A: Show](prompt: Prompt[A]) {
      def toIO: IO[A] =
        for {
          strNoSpaces <- IO(
            StdIn
              .readLine(s"${prompt.question} [enter for ${prompt.defaultAnswer.show}] >")
              .replaceAll(" ", "")
          )
          res <- strNoSpaces match {
            case ""    => prompt.defaultAnswer.pure[IO]
            case other => IO.fromTry(Try(prompt.parseAnswer(other)))
          }
        } yield res
    }
  }
}
