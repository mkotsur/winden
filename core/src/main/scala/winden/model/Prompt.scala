package io.github.mkotsur
package winden.model

import winden.model.TimePiece.implicits._

import cats.effect.IO
import cats.implicits.{catsSyntaxEq, toShow}

import java.time.{LocalDate, YearMonth}

object Prompt {
  object dict {

    def descPrompt: Prompt[String] = Prompt("Description", identity, "")

    def reportWeekend: Prompt[Boolean] =
      Prompt(
        question = "Do you want to report any weekend hours [no]?",
        _.toLowerCase === "yes",
        false
      )

    def dailyDescription: Prompt[Boolean] =
      Prompt(
        question = "Do you want to add daily descriptions [no]?",
        _.toLowerCase === "yes",
        false
      )

    def currentMonthPrompt: Prompt[YearMonth] =
      Prompt("Month YYYY.MM?", YearMonth.parse(_, formats.`YYYY.MM`), YearMonth.now)

    def piecePrompt(localDate: LocalDate): Prompt[Byte] =
      Prompt(
        s"Hours on ${localDate.getDayOfMonth}  ${localDate.getDayOfWeek}",
        java.lang.Byte.parseByte,
        0.toByte
      )

    //Todo: refactor to prompt
    def summaryIO(pieces: List[TimePiece]): IO[Unit] =
      for {
        _ <- IO(println(pieces.show))
        _ <- IO(println("----------------------------------------------"))
        _ <- IO(println(s"Total: ${pieces.foldLeft(0)(_ + _.hours)}h"))
      } yield ()

  }
}

case class Prompt[A](question: String, parseAnswer: String => A, defaultAnswer: A)
