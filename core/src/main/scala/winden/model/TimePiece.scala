package io.github.mkotsur.winden.model

import cats.Show
import cats.implicits.toShow

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

case class TimePiece(hours: Byte, day: LocalDate, description: String)

object TimePiece {

  private object formats {
    val reportFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH)
  }

  object implicits {
    implicit val timePieceShow: Show[TimePiece] = Show.show { tp =>
      s"${tp.day.format(formats.reportFormat)} \t ${tp.hours}h \t ${tp.description}"
    }
    implicit val timePiecesShow: Show[List[TimePiece]] = Show.show { tps =>
      tps.map(_.show).mkString("\n")
    }
  }
}
