package io.github.mkotsur
package winden.service

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers._

import java.time.{LocalDate, YearMonth}

class PersonalAssistantSpec extends AnyFunSpec {
  describe("PersonalAssistant") {

    describe("in Jan 2021") {
      val ym = YearMonth.of(2021, 1)

      it("should return 21 business days without weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = false)
        allBusinessDays.size shouldBe 21
      }
      it("should return 31 business days without weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = true)
        allBusinessDays.size shouldBe 31
      }

      it("should contain 29.01 at the end of the list without weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = false)
        allBusinessDays.last shouldBe LocalDate.of(2021, 1, 29)
      }
      it("should contain 29.01 at the end of the list with weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = true)
        allBusinessDays.last shouldBe LocalDate.of(2021, 1, 31)
      }

      it("should contain 01.01 at the beginning of the list without weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = false)
        allBusinessDays.head shouldBe LocalDate.of(2021, 1, 1)
      }

      it("should contain 01.01 at the beginning of the list with weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = true)
        allBusinessDays.head shouldBe LocalDate.of(2021, 1, 1)
      }
    }

    describe("in Mar 2021") {
      val ym = YearMonth.of(2021, 3)

      it("should contain 31.03 at the end of the list without weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = false)
        allBusinessDays.last shouldBe LocalDate.of(2021, 3, 31)
      }

      it("should contain 31.03 at the end of the list with weekends") {
        val allBusinessDays = PersonalAssistant.potentiallyWorkingDays(ym, weekends = true)
        allBusinessDays.last shouldBe LocalDate.of(2021, 3, 31)
      }

    }
  }
}
