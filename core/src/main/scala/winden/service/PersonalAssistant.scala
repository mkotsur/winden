package io.github.mkotsur
package winden.service

import java.time.{DayOfWeek, LocalDate, YearMonth}

object PersonalAssistant {

  private val weekend = List(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

  def potentiallyWorkingDays(ym: YearMonth, weekends: Boolean): List[LocalDate] =
    Range(1, ym.atEndOfMonth().getDayOfMonth + 1)
      .map(ym.atDay)
      .filterNot(day => !weekends && weekend.contains(day.getDayOfWeek))
      .toList

}
