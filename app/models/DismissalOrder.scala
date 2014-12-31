package models

import java.time.LocalDate
import java.util.Date

import models.Database._
import org.squeryl.{Session, Query}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}
import scala.collection.Iterable

case class DismissalOrder(
                           id: Long,
                           order_type_id: Int,
                           date_of_order: LocalDate,
                           leaving_reason_id: Int,
                           position_id: Int,
                           employee_id: Long,
                           comment: String,
                           leaving_date: LocalDate,
                           override var created_at: TimeStamp,
                           override var updated_at: TimeStamp
                           ) extends Entity[Long] {

  override def save = transaction {
    val dismissalOrder = super.save.asInstanceOf[DismissalOrder]
    Employee.findById(dismissalOrder.employee_id).map { employee =>
      Position.findById(employee.positionHistoryId.get).map { position =>
        Structure.findById(position.position_id).map { structure =>
          employee.copy(positionHistoryId = None).update
          structure.copy(positionHistoryId = None).update
          position.copy(close_date = Some(dismissalOrder.leaving_date),
            dismissal_order_id = Some(dismissalOrder.id)).update
        }
      }
    }
    dismissalOrder
  }

  def update = inTransaction {
    DismissalOrder.findById(this.id).map { dismissalOrder =>
      val tmp = this.copy(created_at = dismissalOrder.created_at, updated_at = dismissalOrder.updated_at)
      tmp.save
    }
  }
}

object DismissalOrder {

  import Database.{dismissalOrderTable}

  implicit val dismissalWrites: Writes[DismissalOrder] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_type_id").write[Int] and
      (JsPath \ "date_of_order").write[LocalDate] and
      (JsPath \ "leaving_reason_id").write[Int] and
      (JsPath \ "position_id").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "comment").write[String] and
      (JsPath \ "leaving_date").write[LocalDate] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(DismissalOrder.unapply))

  implicit val dismissalReads: Reads[DismissalOrder] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_type_id").read[Int] and
      (JsPath \ "date_of_order").read[LocalDate] and
      (JsPath \ "leaving_reason_id").read[Int] and
      (JsPath \ "position_id").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "comment").read[String] and
      (JsPath \ "leaving_date").read[LocalDate] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(DismissalOrder.apply _)

  def allQ: Query[DismissalOrder] = from(dismissalOrderTable) {
    dismissalOrder => select(dismissalOrder)
  }

  def findAll: Iterable[DismissalOrder] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(dismissalOrderTable) {
      dismissalOrder => where(dismissalOrder.id === id) select (dismissalOrder)
    }.headOption
  }

  def findFull = inTransaction {
    from(dismissalOrderTable, employeeTable, structureTable) {
      (dismissalOrder, employee, structure) =>
        where((employee.id === dismissalOrder.employee_id)
          and (dismissalOrder.position_id === structure.id)) select(dismissalOrder, employee, structure)
    }.toList
  }
}

case class LeavingReason(
                          id: Int,
                          punkt: String,
                          name: String,
                          override var created_at: TimeStamp,
                          override var updated_at: TimeStamp
                          ) extends Entity[Int] {

  override def save = inTransaction {
    super.save.asInstanceOf[LeavingReason]
  }

  def update = inTransaction {
    LeavingReason.findById(this.id).map { leavingReason =>
      val tmp = this.copy(created_at = leavingReason.created_at, updated_at = leavingReason.updated_at)
      tmp.save
    }
  }
}

object LeavingReason {

  import Database.{leavingReasonTable}

  implicit val leavingReasonWrites: Writes[LeavingReason] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "punkt").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(LeavingReason.unapply))

  implicit val leavingReasonReads: Reads[LeavingReason] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "punkt").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(LeavingReason.apply _)

  def allQ: Query[LeavingReason] = from(leavingReasonTable) {
    leavingReason => select(leavingReason)
  }

  def findAll: Iterable[LeavingReason] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(leavingReasonTable) {
      leavingReason => where(leavingReason.id === id) select (leavingReason)
    }.headOption
  }

}

