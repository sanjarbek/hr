package controllers


import java.time.{LocalTime, DayOfWeek, LocalDate}
import java.time.temporal.WeekFields

import models.Database.MyLocalDate
import models.{Calendar, DayType, CalendarType}
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object Calendars extends Controller {

  def createCalendarType = Action {
    val startTime = LocalTime.now()

    val calendarType = 1
    val year = 2014
    val lengthOfYear = LocalDate.now().lengthOfYear()
    val buf = scala.collection.mutable.ListBuffer.empty[Calendar]

    for (dayOfYear <- 1 to lengthOfYear) {
      val date = LocalDate.ofYearDay(year, dayOfYear)

      if (date.getDayOfWeek == DayOfWeek.SATURDAY || date.getDayOfWeek == DayOfWeek.SUNDAY) {
        buf += Calendar(0, calendarType, date, 2, null, null).save
      }
      else {
        buf += Calendar(0, calendarType, date, 1, null, null).save
      }
    }

    Ok(LocalTime.now().compareTo(startTime).toString)


    Calendar.findById(460).map { dayType =>
      val json = Json.toJson(dayType)
      val normal = json.\("calendar_date").as[MyLocalDate]
      Logger.info(normal.toString)
      Ok(normal.plusDays(100).toString)
    }.getOrElse(NotFound(Json.toJson("Не найден такой тип календарного дня.")))

  }

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

  def jsonCalendarTypes() = Action {
    val calendarTypes = CalendarType.findAll.map { calendarType => Json.toJson(calendarType)}
    Ok(Json.toJson(calendarTypes))
  }
}
