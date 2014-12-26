package controllers

import java.time._

import models.Database.MyLocalDate
import models.{Structure, WorkingSheetDay, Employee}
import org.squeryl.Session
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Table, Schema}

import scala.collection.mutable.ListBuffer

object WorkingSheets extends Controller {

  def workingDayList() = Action {
    Ok(views.html.working_sheet.test())
  }

  def workingDayListJson(structureId: Option[Int]) = Action {

    val year = 2015
    val month = 1

    val beginMonthDate = java.util.Date.from(LocalDate.parse("2014-12-01").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
    val endMonthDate = java.util.Date.from(LocalDate.parse("2014-12-31").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)


    val structuresId = structureId match {
      case Some(i) => Structure.findChilds(i).filter(_.structure_type == 4).map(_.id)
      case None => Structure.findAll.filter(_.structure_type == 4).map(_.id)
    }

    val employees = inTransaction {
      //      Session.currentSession.setLogger(msg => println(msg))
      from(models.Database.employeeTable, models.Database.positionTable) { (emp, ph) =>
        where(ph.position_id.in(structuresId)
          and (ph.close_date.isNull or ph.close_date >= Some(beginMonthDate))
          and (ph.start_date <= endMonthDate)
          and emp.id === ph.employee_id) select (emp)
      }.toList
    }

    val zzz = transaction {
      from(models.Database.employeeTable, models.Database.workingSheetDayTable)((emp, wsd) =>
        where(emp.id.in(employees.map(_.id)) and (emp.id === wsd.employee_id))
          select(emp, wsd)
      ).toList
    }

    val a = scala.collection.mutable.Map[Employee, ListBuffer[WorkingSheetDay]]()

    for ((k, v) <- zzz) {
      a.get(k).map(found => found += v).getOrElse(a += (k -> ListBuffer(v)))
    }

    val jsonData = a.map { result =>
      Json.obj(
        "employee" -> Json.toJson(result._1),
        "workingSheetDays" -> result._2.map(Json.toJson(_))
      )
    }

    Ok(Json.toJson(jsonData))
  }

  def generateWorkingMonth() = Action {
    val tabelMonth = LocalDate.now
    val lengthOfMonth = tabelMonth.lengthOfMonth
    for (dayOfMonth <- 1 to lengthOfMonth) {
      val workingDate = LocalDate.of(tabelMonth.getYear, tabelMonth.getMonthValue, dayOfMonth)
      if (workingDate.getDayOfWeek == DayOfWeek.SATURDAY || workingDate.getDayOfWeek == DayOfWeek.SUNDAY) {
        WorkingSheetDay(0, 1, workingDate, 2, 0, null, null).save
      } else {
        WorkingSheetDay(0, 1, workingDate, 1, 8, null, null).save
      }
    }
    Ok("Finished.")
  }
}
