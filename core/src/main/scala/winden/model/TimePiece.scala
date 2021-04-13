package io.github.mkotsur.winden.model

import cats.Show

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

case class TimePiece(hours: Byte, day: LocalDate, description: String)

object TimePiece {

  private object formats {
    val reportFormat = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH)
  }

  object implicits {
    implicit val timePieceShow: Show[TimePiece] = Show.show { tp =>
      s"${tp.day.format(formats.reportFormat)} \t ${tp.hours}h \t ${tp.description}"
    }
  }
}
