package controllers

import java.time._

import models.{Employee, DayType, WorkingSheetDay, Structure}
import org.squeryl.Session
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import org.squeryl.{Table, Schema}
import models.MyCustomTypes._
import scala.collection.mutable.ListBuffer

object WorkingSheets extends Controller {

  def workingDayList() = Action {
    Ok(views.html.working_sheet.working_sheet())
  }

  def workingDayListJson(structureId: Option[Int]) = Action {

    val year = 2015
    val month = 1

    val beginMonthDate = LocalDate.of(year, month, 1)
    val endMonthDate = LocalDate.of(year, month, 31)

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
      from(models.Database.employeeTable, models.Database.workingSheetDayTable, models.Database.dayTypeTable)((emp, wsd, dayType) =>
        where(emp.id.in(employees.map(_.id)) and (emp.id === wsd.employee_id) and (wsd.day_type === dayType.id))
          select(emp, wsd, dayType) orderBy(emp.id, wsd.working_day)
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


    val tabelMonth = LocalDate.now
    val lengthOfMonth = tabelMonth.lengthOfMonth

    val beginMonthDate = LocalDate.of(year, month, 1)
    val endMonthDate = LocalDate.of(year, month, lengthOfMonth)

    // Находим список должностей для указанного подразделения (филиала).
    // Если подразделение (филиал) не указан, то выбираем все должности.
    val structuresId = structureId match {
      case Some(i) => Structure.findChilds(i).filter(_.structure_type == 4).map(_.id)
      case None => Structure.findAll.filter(_.structure_type == 4).map(_.id)
    }

    // Удаляем записи из таблицы табеля для указаного месяца и года.
    // Пока удаляются все записи. В будущем надо будет сделать чтобы удалялись только для указанного подразделения.
    WorkingSheetDay.deleteMonthData(LocalDate.now)


    // Находим список сотрудников
    val emp_pos = transaction {
      //      Session.currentSession.setLogger(msg => println(msg))
      from(models.Database.employeeTable, models.Database.positionTable) { (emp, ph) =>
        where(ph.position_id.in(structuresId)
          and (ph.close_date.isNull or ph.close_date >= Some(beginMonthDate))
          and (ph.start_date <= endMonthDate)
          and (emp.id === ph.employee_id)
        ) select(emp, ph)
      }.toList
    }

    implicit class RichLocalDate(d: LocalDate) {
      def between(startDate: LocalDate, endDate: LocalDate): Boolean = {
        if ((d.isEqual(startDate) || d.isAfter(startDate)) && (d.isEqual(endDate) || d.isBefore(endDate)))
          true
        else
          false
      }
    }

    // Заполняем табель для найденных сотрудников за текущий месяц.
    for ((emp, pos) <- emp_pos) {
      val startDate = pos.start_date
      val endDate = pos.close_date.getOrElse(pos.end_date.getOrElse(endMonthDate))

      for (dayOfMonth <- 1 to lengthOfMonth) {
        val workingDate = LocalDate.of(tabelMonth.getYear, tabelMonth.getMonthValue, dayOfMonth)

        WorkingSheetDay.findByEmployeeAndDate(emp.id, workingDate).map{ sheetDay =>
          if (workingDate.between(startDate, endDate)) {
            workingDate.getDayOfWeek match {
              case DayOfWeek.SATURDAY | DayOfWeek.SUNDAY => sheetDay.copy(day_type = 4, hours = 0).update
              case _ => sheetDay.copy(day_type = 1, hours = 8).update
            }
          }
        }.getOrElse{
          if (workingDate.between(startDate, endDate)) {
            workingDate.getDayOfWeek match {
              case DayOfWeek.SATURDAY | DayOfWeek.SUNDAY => WorkingSheetDay(0, emp.id, workingDate, 4, 0, null, null).save
              case _ => WorkingSheetDay(0, emp.id, workingDate, 1, 8, null, null).save
            }
          }
          else {
            WorkingSheetDay(0, emp.id, workingDate, 10, 0, null, null).save
          }
        }
      }
    }

    Ok("Finished.")
  }

  def updateWorkSheetDayDayType = Action(parse.json) { implicit request =>

    case class Test(sheetDayId: Long, dayTypeId: Int)
    implicit val testReads: Reads[Test] = (
      (JsPath \ "sheetDayId").read[Long] and
        (JsPath \ "dayTypeId").read[Int]
      )(Test.apply _)
    implicit val testWrites: Writes[Test] = (
      (JsPath \ "sheetDayId").write[Long] and
        (JsPath \ "dayTypeId").write[Int]
      )(unlift(Test.unapply))

    val valueJson = request.body

    valueJson.validate[Test].fold(
      valid = { test =>
        WorkingSheetDay.findById(test.sheetDayId).map { sheetDay =>
          DayType.findById(test.dayTypeId).map { dayType =>
            val tmp = sheetDay.copy(day_type = test.dayTypeId).update
            Ok(Json.toJson("True"))
          }.getOrElse(NotFound(Json.toJson("Такой тип дня не существует.")))
        }.getOrElse(NotFound(Json.toJson("Такого дня в базе не существует.")))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def updateWorkSheetDayHours = Action(parse.json) { implicit request =>

    case class Test(sheetDayId: Long, hours: Int)
    implicit val testReads: Reads[Test] = (
      (JsPath \ "sheetDayId").read[Long] and
        (JsPath \ "hours").read[Int]
      )(Test.apply _)
    implicit val testWrites: Writes[Test] = (
      (JsPath \ "sheetDayId").write[Long] and
        (JsPath \ "hours").write[Int]
      )(unlift(Test.unapply))

    val valueJson = request.body

    valueJson.validate[Test].fold(
      valid = { test =>
        WorkingSheetDay.findById(test.sheetDayId).map { sheetDay =>
          val tmp = sheetDay.copy(hours = test.hours).update
          Ok(Json.toJson("True"))
        }.getOrElse(NotFound(Json.toJson("Такого дня в базе не существует.")))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

}
