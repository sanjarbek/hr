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
                  order_category: Option[Int],
                  name: Option[String],
                  nomer: Option[Int],
                  date_of_order: Option[Date],
                  content: Option[String],
                  tags: Option[String]
                  ) extends KeyedEntity[Long]

object Order {

  import Database.{orderTable}

  implicit val contractWrites: Writes[Order] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_category").write[Option[Int]] and
      (JsPath \ "name").write[Option[String]] and
      (JsPath \ "nomer").write[Option[Int]] and
      (JsPath \ "date_of_order").write[Option[Date]] and
      (JsPath \ "content").write[Option[String]] and
      (JsPath \ "tags").write[Option[String]]
    )(unlift(Order.unapply))

  implicit val contractReads: Reads[Order] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_category").read[Option[Int]] and
      (JsPath \ "name").read[Option[String]] and
      (JsPath \ "nomer").read[Option[Int]] and
      (JsPath \ "date_of_order").read[Option[Date]] and
      (JsPath \ "content").read[Option[String]] and
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
