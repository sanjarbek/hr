package controllers


import java.time.{LocalTime, DayOfWeek, LocalDate}
import java.time.temporal.WeekFields

import models.Database.MyLocalDate
import models.{Calendar, DayType, CalendarType}
import play.api.Logger
import play.api.libs.json.{JsValue, JsError, Json}
import play.api.mvc.{Action, Controller}

object Calendars extends Controller {

  def saveCalendarType = Action(parse.json) { implicit request =>
    val calendarTypeJson = request.body
    calendarTypeJson.validate[CalendarType].fold(
      valid = { calendarType =>
        val tmp: CalendarType = calendarType.save
        Ok(Json.toJson(tmp))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def showCalendarType = Action {
    Ok(views.html.calendar.calendar_type.show())
  }

  def showCalendarByTypeAndYear(calendarTypeId: Int, year: Int) = Action {
    val calendarYearDays = Calendar.findCalendarByType(calendarTypeId).filter(_.calendar_date.getYear == year).map { calendarYear => Json.toJson(calendarYear)}
    Ok(Json.toJson(calendarYearDays))
  }

  def jsonCalendarTypes() = Action {
    val calendarTypes = CalendarType.findAll.map { calendarType => Json.toJson(calendarType)}
    Ok(Json.toJson(calendarTypes))
  }

  def jsonCalendarDayTypes() = Action {
    val dayTypes = DayType.findAll.filter(_.dayType).map { dayType => Json.toJson(dayType)}
    Ok(Json.toJson(dayTypes))
  }

  def updateCalendarDay = Action(parse.json) { implicit request =>
    val calendarDayJson = request.body
    calendarDayJson.validate[Calendar].fold(
      valid = { calendarDay =>
        val tmp: Calendar = calendarDay.save
        Ok(Json.toJson(tmp))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def jsonCalendarTypeYears(calendarTypeId: Int) = Action {
    val years: List[Int] = Calendar.findCalendarTypeYears(calendarTypeId).map { calendarDate => calendarDate.calendar_date.getYear}
    Ok(Json.toJson(years.distinct))
  }

  def create(calendarTypeId: Int, year: Int) = Action {
    val lengthOfYear = LocalDate.of(year, 1, 1).lengthOfYear()
    for (dayOfYear <- 1 to lengthOfYear) {
      val date = LocalDate.ofYearDay(year, dayOfYear)

      if (date.getDayOfWeek == DayOfWeek.SATURDAY || date.getDayOfWeek == DayOfWeek.SUNDAY) {
        Calendar(0, calendarTypeId, date, 2, null, null).save
      }
      else {
        Calendar(0, calendarTypeId, date, 1, null, null).save
      }
    }
    Ok(Json.toJson(year))
  }

}
