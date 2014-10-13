package models

import java.util.Date

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
                  order_category: Int,
                  name: String,
                  nomer: Int,
                  date_of_order: Date,
                  content: String,
                  tags: Option[String]
                  ) extends KeyedEntity[Long]

object Order {

  import Database.{orderTable}

  implicit val contractWrites: Writes[Order] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_category").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "nomer").write[Int] and
      (JsPath \ "date_of_order").write[Date] and
      (JsPath \ "content").write[String] and
      (JsPath \ "tags").write[Option[String]]
    )(unlift(Order.unapply))

  implicit val contractReads: Reads[Order] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_category").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "nomer").read[Int] and
      (JsPath \ "date_of_order").read[Date] and
      (JsPath \ "content").read[String] and
      (JsPath \ "tags").read[Option[String]]
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

  def insert(order: Order): Order = inTransaction {
    orderTable.insert(order)
  }

  def update(order: Order) {
    inTransaction {
      orderTable.update(order)
    }
  }

  def delete(id: Long) = inTransaction {
    orderTable.deleteWhere(order => order.id === id)
  }
}
