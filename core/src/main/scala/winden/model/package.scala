package io.github.mkotsur
package winden

import cats.Show

import java.time.YearMonth
import java.time.format.DateTimeFormatter

package object model {
  private[model] object formats {
    val `YYYY.MM` = DateTimeFormatter.ofPattern("yyyy.MM")
  }

  object jdktime {
    object implicits {
      implicit val showMonth: Show[YearMonth] = new Show[YearMonth] {
        override def show(t: YearMonth): String = t.format(formats.`YYYY.MM`)
      }
    }
  }
}
