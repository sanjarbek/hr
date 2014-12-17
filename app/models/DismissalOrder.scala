package models

import java.util.Date

import models.Database.{MyLocalDate, TimeStamp}
import org.squeryl.{Query}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}
import scala.collection.Iterable

case class DismissalOrder(
                           id: Long,
                           order_type_id: Int,
                           nomer: Int,
                           date_of_order: Date,
                           leaving_reason_id: Int,
                           employee_id: Long,
                           comment: String,
                           leaving_date: Date,
                           override var created_at: TimeStamp,
                           override var updated_at: TimeStamp
                           ) extends Entity[Long] {

  override def save = inTransaction {
    super.save.asInstanceOf[DismissalOrder]
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
      (JsPath \ "nomer").write[Int] and
      (JsPath \ "date_of_order").write[Date] and
      (JsPath \ "leaving_reason_id").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "comment").write[String] and
      (JsPath \ "leaving_date").write[Date] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(DismissalOrder.unapply))

  implicit val dismissalReads: Reads[DismissalOrder] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_type_id").read[Int] and
      (JsPath \ "nomer").read[Int] and
      (JsPath \ "date_of_order").read[Date] and
      (JsPath \ "leaving_reason_id").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "comment").read[String] and
      (JsPath \ "leaving_date").read[Date] and
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
}