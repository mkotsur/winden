package io.github.mkotsur.winden.model

import cats.Show

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class TimePiece(hours: Byte, day: LocalDate, description: String)

object TimePiece {
  object implicits {
    implicit val timePieceShow: Show[TimePiece] = Show.show { tp =>
      s"${tp.day.format(DateTimeFormatter.ISO_DATE)} \t ${tp.hours}h \t ${tp.description}"
    }
  }
}
