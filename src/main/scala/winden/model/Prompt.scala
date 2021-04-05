package io.github.mkotsur
package winden.model

import cats.implicits.catsSyntaxEq

import java.time.{LocalDate, YearMonth}

object Prompt {
  object dict {

    def descPrompt = Prompt("Description", identity, "")

    def reportWeekend =
      Prompt(question = "Do you want to report any weekend hours [no]?", _.toLowerCase === "yes", false)

    def dailyDescription =
      Prompt(question = "Do you want to add daily descriptions [no]?", _.toLowerCase === "yes", false)

    def prevMonthPrompt = {
      val prevMonth = YearMonth.now().minusMonths(1)
      Prompt("Month YYYY.MM?", YearMonth.parse(_, formats.`YYYY.MM`), prevMonth)
    }

    def timePiecePrompt(localDate: LocalDate): Prompt[Byte] =
      Prompt(s"Hours on ${localDate.getDayOfMonth}  ${localDate.getDayOfWeek}", java.lang.Byte.parseByte, 0.toByte)

  }
}

case class Prompt[A](question: String, parseAnswer: String => A, defaultAnswer: A)
