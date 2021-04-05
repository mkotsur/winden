package io.github.mkotsur
package winden.service

import java.time.{DayOfWeek, LocalDate, Period, YearMonth}

object PersonalAssistant {

  private val weekend = List(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

  def allBusinessDays(ym: YearMonth): List[LocalDate] =
    Range(1, ym.atEndOfMonth().getDayOfMonth + 1)
      .map(ym.atDay)
      .filterNot(day => weekend.contains(day.getDayOfWeek))
      .toList

}
