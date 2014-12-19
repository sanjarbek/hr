package models

import java.util.Date

import models.Database.TimeStamp
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable

case class Position(
                     id: Long,
                     employment_order_id: Long,
                     position_id: Int,
                     employee_id: Long,
                     dismissal_order_id: Option[Long],
                     start_date: Date,
                     end_date: Option[Date],
                     close_date: Option[Date],
                     override var created_at: TimeStamp,
                     override var updated_at: TimeStamp
                     ) extends Entity[Long] {
  override def save = inTransaction {
    super.save.asInstanceOf[Position]
  }

  def update = inTransaction {
    Position.findById(this.id).map { position =>
      val tmp = this.copy(created_at = position.created_at, updated_at = position.updated_at)
      tmp.save
    }
  }
}

object Position {

  import Database.{positionTable, formatTimeStamp}

  implicit val positionWrites: Writes[Position] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employment_order_id").write[Long] and
      (JsPath \ "position_id").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "dismissal_order_id").write[Option[Long]] and
      (JsPath \ "start_date").write[Date] and
      (JsPath \ "end_date").write[Option[Date]] and
      (JsPath \ "close_date").write[Option[Date]] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(Position.unapply))

  implicit val positionReads: Reads[Position] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employment_order_id").read[Long] and
      (JsPath \ "position_id").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "dismissal_order_id").read[Option[Long]] and
      (JsPath \ "start_date").read[Date] and
      (JsPath \ "end_date").read[Option[Date]] and
      (JsPath \ "close_date").read[Option[Date]] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(Position.apply _)

  def allQ: Query[Position] = from(positionTable) {
    position => select(position)
  }

  def findAll: Iterable[Position] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(positionTable) {
      position => where(position.id === id) select (position)
    }.headOption
  }

  def findByEmploymentOrderId(employmentOrderId: Long) = inTransaction {
    from(positionTable) {
      position => where(position.employment_order_id === employmentOrderId) select (position)
    }.headOption
  }

  def findByEmployeeId(employeeId: Long) = inTransaction {
    from(positionTable) {
      position => where(position.employee_id === employeeId) select (position)
    }.toList
  }

  def findByDismissalOrderId(dismissalOrderId: Long) = inTransaction {
    from(positionTable) {
      position => where(position.dismissal_order_id === dismissalOrderId) select (position)
    }.headOption
  }

}
