package models

import java.time.{LocalDateTime, LocalDate}

import models.Database._
import models.MyCustomTypes._
import org.squeryl.Query
import org.squeryl.annotations._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

import scala.collection.Iterable

case class WorkingSheetDay(
                            id: Long,
                            employee_id: Long,
                            working_day: LocalDate,
                            day_type: Int,
                            hours: Int,
                            override var created_at: LocalDateTime,
                            override var updated_at: LocalDateTime
                            ) extends Entity[Long] {

  override def save = inTransaction {
    super.save.asInstanceOf[WorkingSheetDay]
  }

  def update = inTransaction {
    WorkingSheetDay.findById(this.id).map { workingSheetDay =>
      val tmp = this.copy(created_at = workingSheetDay.created_at, updated_at = workingSheetDay.updated_at)
      tmp.save
    }
  }
}

object WorkingSheetDay {

  import Database.{workingSheetDayTable}

  implicit val workingSheetDayWrites: Writes[WorkingSheetDay] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "working_day").write[LocalDate] and
      (JsPath \ "day_type").write[Int] and
      (JsPath \ "hours").write[Int] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(WorkingSheetDay.unapply))

  implicit val workingSheetDayReads: Reads[WorkingSheetDay] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "working_day").read[LocalDate] and
      (JsPath \ "day_type").read[Int] and
      (JsPath \ "hours").read[Int] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
    )(WorkingSheetDay.apply _)

  def allQ: Query[WorkingSheetDay] = from(workingSheetDayTable) {
    workingSheetDay => select(workingSheetDay)
  }

  def findAll: Iterable[WorkingSheetDay] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(workingSheetDayTable) {
      workingSheetDay => where(workingSheetDay.id === id) select (workingSheetDay)
    }.headOption
  }

  def deleteMonthData(date: java.time.LocalDate) = inTransaction {
    workingSheetDayTable.deleteWhere(wsdt =>
      wsdt.id <> 0
      //      (wsdt.working_day.getMonthValue===date.getMonthValue)
      //        and (wsdt.working_day.getYear===date.getYear)
    )
  }

}
