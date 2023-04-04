package io.github.mkotsur
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import winden.model.Prompt.dict._
import winden.model._
import winden.service.PersonalAssistant
import winden.model.jdktime.implicits._
import winden.implicits._
import winden.service.Persistence

import java.time.{LocalDate, YearMonth}

object Winden extends IOApp {

  private def reportMonth(month: YearMonth): IO[Unit] =
    for {
      includeDesc <- dailyDescription.toIO
      loadedPieces <- Persistence.load(month).map {
        case None                        => Nil
        case Some(pieces) if includeDesc => pieces
        case Some(pieces)                => pieces.map(_.copy(description = ""))
      }
      _ <- Prompt.dict.summaryIO(loadedPieces)
    } yield ()

  private def promptRemainingDays(
      month: YearMonth,
      loadedPieces: List[TimePiece],
      filteredDays: List[LocalDate]
  ): IO[Unit] =
    for {
      dailyDescriptions <- dailyDescription.toIO
      _                 <- IO(println(s"Timesheet for ${month.getMonth.name()} ${month.getYear}"))
      _                 <- Prompt.dict.summaryIO(loadedPieces)
      newPieces <- {
        def processDays(
            days: List[LocalDate],
            accIO: IO[List[TimePiece]] = IO.pure(Nil)
        ): IO[List[TimePiece]] =
          days match {
            case nxt :: rest =>
              (for {
                timePiece <- piecePrompt(nxt).toIO
                description <-
                  if (dailyDescriptions && timePiece > 0) descPrompt.toIO else "".pure[IO]
                r <- processDays(
                  rest,
                  accIO.map(_ :+ TimePiece(timePiece, nxt, description))
                )
              } yield r).recoverWith(_ =>
                for {
                  _    <- IO(println("Storing intermediate results"))
                  acc  <- accIO
                  file <- Persistence.store(month, loadedPieces ++ acc)
                  _    <- IO(println(s"Written into $file"))
                } yield acc
              )
            case Nil => accIO
          }

        processDays(filteredDays)
      }
      _    <- Prompt.dict.summaryIO(loadedPieces ++ newPieces)
      file <- Persistence.store(month, loadedPieces ++ newPieces)
      _    <- IO(println(s"Written into $file"))
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    for {
      month        <- currentMonthPrompt.toIO
      loadedPieces <- Persistence.load(month).map(_.getOrElse(Nil))
      weekends     <- reportWeekend.toIO
      filteredDays =
        PersonalAssistant
          .potentiallyWorkingDays(month, weekends)
          .filterNot(d => loadedPieces.map(_.day).contains(d))
      _ <-
        if (filteredDays.isEmpty)
          reportMonth(month)
        else
          promptRemainingDays(month, loadedPieces, filteredDays)
    } yield ExitCode.Success

}
