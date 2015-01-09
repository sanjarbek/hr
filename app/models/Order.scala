package models

import java.util.Date

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable
import java.time.{LocalDateTime, LocalDate}
import MyCustomTypes._

case class Order(
                  id: Long,
                  order_type_id: Int,
                  nomer: Int,
                  date_of_order: LocalDate,
                  override var created_at: LocalDateTime,
                  override var updated_at: LocalDateTime
                  ) extends Entity[Long]

object Order {

  import Database.{orderTable}
  import MyCustomTypes._


  implicit val orderWrites: Writes[Order] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_type_id").write[Int] and
      (JsPath \ "nomer").write[Int] and
      (JsPath \ "date_of_order").write[LocalDate] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(Order.unapply))

  implicit val orderReads: Reads[Order] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_type_id").read[Int] and
      (JsPath \ "nomer").read[Int] and
      (JsPath \ "date_of_order").read[LocalDate] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
    )(Order.apply _)

  def allQ: Query[Order] = from(orderTable) {
    order => select(order)
  }

  def findAll: Iterable[Order] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(orderTable) {
      order => where(order.id === id) select (order)
    }.headOption
  }
}
