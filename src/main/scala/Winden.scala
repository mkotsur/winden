package io.github.mkotsur

import winden.service.PersonalAssistant

import cats.effect.{ExitCode, IO, IOApp}
import winden.model.TimePiece
import winden.model.jdktime.implicits._

import TimePiece.implicits._
import winden.model.Prompt.dict.{descPrompt, _}

import cats.implicits._
import winden.cli.effects.meow.implicits._

object Winden extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      month             <- prevMonthPrompt.toIO
      weekends          <- reportWeekend.toIO
      dailyDescriptions <- dailyDescription.toIO
      days              <- PersonalAssistant.potentiallyWorkingDays(month, weekends).pure[IO]
      _                 <- IO(println(s"Timesheet for ${month.getMonth.name()} ${month.getYear}"))
      timePieces <- days.map { localDate =>
        for {
          hours <- timePiecePrompt(localDate).toIO
          desc  <- if (dailyDescriptions) descPrompt.toIO else "".pure[IO]
        } yield TimePiece(hours, localDate, desc)
      }.sequence
      _ <- IO(println(timePieces.map(_.show).mkString("\n")))
      _ <- IO(println("----------------------------------------------"))
      _ <- IO(println(s"Total: ${timePieces.map(_.hours).sum}h"))
    } yield ExitCode.Success

}
