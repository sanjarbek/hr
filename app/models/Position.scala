package models

import java.time.{LocalDateTime, LocalDate}

import models.MyCustomTypes._
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
                     employment_order_id: Option[Long],
                     position_id: Int,
                     employee_id: Long,
                     dismissal_order_id: Option[Long],
                     transfer_order_id: Option[Long],
                     start_date: LocalDate,
                     end_date: Option[LocalDate],
                     close_date: Option[LocalDate],
                     override var created_at: LocalDateTime,
                     override var updated_at: LocalDateTime
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

  import Database.{positionTable}

  implicit val positionWrites: Writes[Position] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employment_order_id").write[Option[Long]] and
      (JsPath \ "position_id").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "dismissal_order_id").write[Option[Long]] and
      (JsPath \ "transfer_order_id").write[Option[Long]] and
      (JsPath \ "start_date").write[LocalDate] and
      (JsPath \ "end_date").write[Option[LocalDate]] and
      (JsPath \ "close_date").write[Option[LocalDate]] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(Position.unapply))

  implicit val positionReads: Reads[Position] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employment_order_id").read[Option[Long]] and
      (JsPath \ "position_id").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "dismissal_order_id").read[Option[Long]] and
      (JsPath \ "transfer_order_id").read[Option[Long]] and
      (JsPath \ "start_date").read[LocalDate] and
      (JsPath \ "end_date").read[Option[LocalDate]] and
      (JsPath \ "close_date").read[Option[LocalDate]] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
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

  def findByTransferEmploymentOrderId(transferOrderId: Long) = inTransaction {
    from(positionTable) { position =>
      where(position.transfer_order_id === transferOrderId and position.employment_order_id.isNull) select (position)
    }.headOption
  }

  def findByTransferDismissalOrderId(transferOrderId: Long) = inTransaction {
    from(positionTable) { position =>
      where(position.transfer_order_id === transferOrderId and position.dismissal_order_id.isNull) select (position)
    }.headOption
  }

}
