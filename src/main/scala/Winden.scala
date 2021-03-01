package io.github.mkotsur

import winden.service.PersonalAssistant

import cats.effect.{ExitCode, IO, IOApp}
import winden.model.TimePiece

import java.time.{LocalDate, YearMonth}
import scala.io.StdIn
import cats.implicits._
import TimePiece.implicits._
import cats.Show
import io.github.mkotsur.winden.cli.Prompt

import java.time.format.DateTimeFormatter
import scala.util.Try
import scala.util.control.Exception.noCatch.desc

object Winden extends IOApp {

  private object formats {
    val `YYYY.MM` = DateTimeFormatter.ofPattern("yyyy.MM")
  }

  private object implicits {
    implicit val showMonth = new Show[YearMonth] {
      override def show(t: YearMonth): String = t.format(formats.`YYYY.MM`)
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {

    val prevMonth = YearMonth.now().minusMonths(1)
    import implicits._

    def monthPrompt(suggestedMonth: YearMonth) =
      Prompt("Month YYYY.MM?", YearMonth.parse(_, formats.`YYYY.MM`), suggestedMonth)

    def timePiecePrompt(localDate: LocalDate): Prompt[Byte] =
      Prompt(s"Hours on ${localDate.getDayOfMonth}  ${localDate.getDayOfWeek}", java.lang.Byte.parseByte, 0.toByte)

    def descPrompt = Prompt("Description", identity, "")

    for {
      month <- monthPrompt(prevMonth).toIO
      days  <- PersonalAssistant.allBusinessDays(month).pure[IO]
      _     <- IO(println(s"Timesheet for ${month.getMonth.name()} ${month.getYear}"))
      timePieces <- days.map { localDate =>
        for {
          hours <- timePiecePrompt(localDate).toIO
          desc  <- descPrompt.toIO
        } yield TimePiece(hours, localDate, desc)
      }.sequence
      _ <- IO(println(timePieces.map(_.show).mkString("\n")))
      _ <- IO(println("----------------------------------------------"))
      _ <- IO(println(s"Total: ${timePieces.map(_.hours).sum}h"))
    } yield ExitCode.Success
  }

}
