package io.github.mkotsur
package winden.service

import java.time.{DayOfWeek, LocalDate, YearMonth}

object WorkDays {

  object filters {

    implicit class PersonalAssistantWithFilters(workDays: List[LocalDate]) {

      def excluding(days: List[LocalDate]): List[LocalDate] =
        workDays.filterNot(d => days.contains(d))
    }

  }

  private val weekend = List(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

  def monthWorkDays(ym: YearMonth, weekends: Boolean): List[LocalDate] =
    Range(1, ym.atEndOfMonth().getDayOfMonth + 1)
      .map(ym.atDay)
      .filterNot(day => !weekends && weekend.contains(day.getDayOfWeek))
      .toList

}
