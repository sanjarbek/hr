package models

import java.util.Date

import models.Database.{MyLocalDate, TimeStamp}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable

case class Order(
                  id: Long,
                  order_type_id: Int,
                  nomer: Int,
                  date_of_order: MyLocalDate,
                  override var created_at: TimeStamp,
                  override var updated_at: TimeStamp
                  ) extends Entity[Long]

object Order {

  import Database.{orderTable, localDateToMyLocalDate, myLocalDateToLocalDate}

  implicit val orderWrites: Writes[Order] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_type_id").write[Int] and
      (JsPath \ "nomer").write[Int] and
      (JsPath \ "date_of_order").write[MyLocalDate] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(Order.unapply))

  implicit val orderReads: Reads[Order] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_type_id").read[Int] and
      (JsPath \ "nomer").read[Int] and
      (JsPath \ "date_of_order").read[MyLocalDate] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
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
