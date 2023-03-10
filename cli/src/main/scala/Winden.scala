package io.github.mkotsur
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import winden.model.Prompt.dict._
import winden.model._
import winden.service.PersonalAssistant
import winden.model.jdktime.implicits._
import winden.implicits._
import winden.service.Persistence

import TimePiece.implicits._

import java.time.LocalDate

object Winden extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      month             <- currentMonthPrompt.toIO
      weekends          <- reportWeekend.toIO
      dailyDescriptions <- dailyDescription.toIO
      loadedPieces      <- Persistence.load(month).map(_.getOrElse(Nil))
      allDays           = PersonalAssistant.potentiallyWorkingDays(month, weekends)
      filteredDays = allDays.filterNot(d => loadedPieces.map(_.day).contains(d))
      _ <- IO(println(s"Timesheet for ${month.getMonth.name()} ${month.getYear}"))
      _ <- IO(println(loadedPieces.show))
      newPieces <- {
        def processDays(
            days: List[LocalDate],
            accIO: IO[List[TimePiece]] = IO.pure(Nil)
        ): IO[List[TimePiece]] =
          days match {
            case nxt :: rest =>
              (for {
                tp <- piecePrompt(nxt).toIO
                description <- if (dailyDescriptions) descPrompt.toIO else "".pure[IO]
                r <- processDays(
                  rest,
                  accIO.map(_ :+ TimePiece(tp, nxt, description))
                )
              } yield r).recoverWith(_ =>
                for {
                  _ <- IO(println("Storing intermediate results"))
                  acc <- accIO
                  file <- Persistence.store(month, loadedPieces ++ acc)
                  _ <- IO(println(s"Written into $file"))
                } yield acc
          )
            case Nil => accIO
          }

        processDays(filteredDays)
      }
      _    <- IO(println((loadedPieces ++ newPieces).show))
      _    <- IO(println("----------------------------------------------"))
      _    <- IO(println(s"Total: ${(loadedPieces ++ newPieces).foldLeft(0)(_ + _.hours)}h"))
      file <- Persistence.store(month, loadedPieces ++ newPieces)
      _    <- IO(println(s"Written into ${file}"))
    } yield ExitCode.Success

}
