package io.github.mkotsur
import winden.config.WindenConf
import winden.implicits._
import winden.model.Prompt.dict._
import winden.model._
import winden.model.jdktime.implicits._
import winden.persistence.DocumentStore.DocumentRoot
import winden.service.{Persistence, WorkDays}

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.time.{LocalDate, YearMonth}

object Winden extends IOApp {

  private implicit def logger = Slf4jLogger.getLogger[IO]

  override def run(args: List[String]): IO[ExitCode] =
    for {
      conf  <- IO.fromEither(WindenConf.load)
      _     <- logger.debug(s"Config: ${conf}")
      month <- currentMonthPrompt.toIO
      loadedPieces <-
        Persistence
          .load(month)(conf.documentRoot)
          .map(_.getOrElse(Nil))
      weekends <- reportWeekend.toIO
      _ <- {
        import WorkDays.filters._
        WorkDays
          .monthWorkDays(month, weekends)
          .excluding(loadedPieces.map(_.day)) match {
          case Nil  => reportPieces(loadedPieces)
          case days => promptDays(month, loadedPieces, days)(conf.documentRoot)
        }
      }
    } yield ExitCode.Success

  private def reportPieces(pieces: List[TimePiece]): IO[Unit] =
    for {
      includeDesc <- dailyDescription.toIO
      loadedPieces =
        if (includeDesc)
          pieces
        else
          pieces.map(_.copy(description = ""))
      _ <- Prompt.dict.summaryIO(loadedPieces)
    } yield ()

  private def promptDays(
      month: YearMonth,
      loadedPieces: List[TimePiece],
      filteredDays: List[LocalDate]
  )(documentRoot: DocumentRoot): IO[Unit] =
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
                  file <- Persistence.store(month, loadedPieces ++ acc)(documentRoot)
                  _    <- IO(println(s"Written into $file"))
                } yield acc
              )
            case Nil => accIO
          }

        processDays(filteredDays)
      }
      _    <- Prompt.dict.summaryIO(loadedPieces ++ newPieces)
      file <- Persistence.store(month, loadedPieces ++ newPieces)(documentRoot)
      _    <- IO(println(s"Written into $file"))
    } yield ()

}
