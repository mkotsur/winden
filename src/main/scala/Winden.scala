package io.github.mkotsur

import winden.service.PersonalAssistant

import cats.effect.{ExitCode, IO, IOApp}
import winden.model.TimePiece

import java.time.{LocalDate, YearMonth}
import scala.io.StdIn
import cats.implicits._
import TimePiece.implicits._

import java.time.format.DateTimeFormatter
import scala.util.Try

object Winden extends IOApp {

  private object formats {
    val `YYYY.MM` = DateTimeFormatter.ofPattern("yyyy.MM")
  }

  override def run(args: List[String]): IO[ExitCode] = {

    val prevMonth = YearMonth.now().minusMonths(1)

    def askMonthIO(suggestedMonth: YearMonth): IO[YearMonth] =
      for {
        monthStrNoSpaces <- IO(
          StdIn
            .readLine(s"Month YYYY.MM? [enter for ${suggestedMonth.format(formats.`YYYY.MM`)}] >")
            .replaceAll(" ", "")
        )
        month <- monthStrNoSpaces match {
          case ""    => suggestedMonth.pure[IO]
          case other => IO.fromTry(Try(YearMonth.parse(other, formats.`YYYY.MM`)))
        }
      } yield month

    def timePiecesIO(days: List[LocalDate]) =
      days.map { localDate =>
        for {
          _           <- IO(println(s" ${localDate.getDayOfMonth}  ${localDate.getDayOfWeek}"))
          hoursStrRaw <- IO(StdIn.readLine("Hours spent? >"))
          hours <- hoursStrRaw match {
            case ""       => 0.toByte.pure[IO]
            case hoursStr => IO(java.lang.Byte.parseByte(hoursStr))
          }
          desc <- IO(StdIn.readLine("Description? >"))
        } yield TimePiece(hours, localDate, desc)
      }.sequence

    for {
      month      <- askMonthIO(prevMonth)
      days       <- PersonalAssistant.allBusinessDays(month).pure[IO]
      _          <- IO(println(s"Timesheet for ${month.getMonth.name()} ${month.getYear}"))
      timePieces <- timePiecesIO(days)
      _          <- IO(println(timePieces.map(_.show).mkString("\n")))
      _          <- IO(println("----------------------------------------------"))
      _          <- IO(println(s"Total: ${timePieces.map(_.hours).sum}h"))
    } yield ExitCode.Success
  }

}
