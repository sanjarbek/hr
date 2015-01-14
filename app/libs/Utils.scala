package libs

import java.time.LocalDate

object Utils {

  implicit class RichLocalDate(d: LocalDate) {
    def between(startDate: LocalDate, endDate: LocalDate): Boolean = {
      if ((d.isEqual(startDate) || d.isAfter(startDate)) && (d.isEqual(endDate) || d.isBefore(endDate)))
        true
      else
        false
    }
  }

}
