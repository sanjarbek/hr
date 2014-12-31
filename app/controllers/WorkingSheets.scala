package controllers

import java.time._

import models._
import org.squeryl.Session
import play.api.Logger
import play.api.libs.json.{JsArray, Json}
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
        where(ph.position_id.in(structuresId)) select (emp)
        //          and (ph.close_date.isNull or ph.close_date >= Some(beginMonthDate))
        //          and (ph.start_date <= endMonthDate)
        //          and emp.id === ph.employee_id) select (emp)
      }.toList
    }

    val zzz = transaction {
      from(models.Database.employeeTable, models.Database.workingSheetDayTable, models.Database.dayTypeTable)((emp, wsd, dayType) =>
        where(emp.id.in(employees.map(_.id)) and (emp.id === wsd.employee_id) and (wsd.day_type === dayType.id))
          select(emp, wsd, dayType)
      ).toList
    }

    val a = scala.collection.mutable.Map[Employee, ListBuffer[(WorkingSheetDay, DayType)]]()

    for ((k, v, d) <- zzz) {
      a.get(k).map(found => found += Tuple2(v, d)).getOrElse(a += (k -> ListBuffer(Tuple2(v, d))))
    }

    val jsonData = a.map { result =>
      Json.obj(
        "employee" -> Json.toJson(result._1),
        "workingSheetDays" -> result._2.map {
          pair => Json.obj(
            "sheetDay" -> Json.toJson(pair._1),
            "dayType" -> Json.toJson(pair._2))
        }
      )
    }

    Ok(Json.toJson(jsonData))
  }

  def generateWorkingMonth(structureId: Option[Int]) = Action {
    val year = 2015
    val month = 1

    val beginMonthDate = java.util.Date.from(LocalDate.parse("2014-12-01").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
    val endMonthDate = java.util.Date.from(LocalDate.parse("2014-12-31").atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)

    val structuresId = structureId match {
      case Some(i) => Structure.findChilds(i).filter(_.structure_type == 4).map(_.id)
      case None => Structure.findAll.filter(_.structure_type == 4).map(_.id)
    }

    WorkingSheetDay.deleteMonthData(LocalDate.now)

    val emp_pos = transaction {
      Session.currentSession.setLogger(msg => println(msg))
      from(models.Database.employeeTable, models.Database.positionTable) { (emp, ph) =>
        where(ph.position_id.in(structuresId)) select(emp, ph)
        //          and (ph.close_date.isNull or ph.close_date >= Some(beginMonthDate))
        //          and (ph.start_date <= endMonthDate)
        //          and (emp.id === ph.employee_id)) select (emp, ph)
      }.toList
    }

    val tabelMonth = LocalDate.now
    val lengthOfMonth = tabelMonth.lengthOfMonth

    for ((emp, pos) <- emp_pos) {
      for (dayOfMonth <- 1 to lengthOfMonth) {
        val workingDate = LocalDate.of(tabelMonth.getYear, tabelMonth.getMonthValue, dayOfMonth)
        if (workingDate.getDayOfWeek == DayOfWeek.SATURDAY || workingDate.getDayOfWeek == DayOfWeek.SUNDAY) {
          WorkingSheetDay(0, emp.id, workingDate, 2, 0, null, null).save
        } else {
          WorkingSheetDay(0, emp.id, workingDate, 1, 8, null, null).save
        }
      }
    }

    Ok("Finished.")
  }

}
