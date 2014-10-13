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

case class OrderTag(
                     id: String,
                     count: Int
                     ) extends KeyedEntity[String]

object OrderTag {

  import Database.{orderTagTable}

  def allQ: Query[OrderTag] = from(orderTagTable) {
    order_tag => select(order_tag)
  }

  def findAll: Iterable[OrderTag] = inTransaction {
    allQ.toList
  }

  def findById(id: String) = inTransaction {
    from(orderTagTable) {
      order_tag => where(order_tag.id === id) select (order_tag)
    }.headOption
  }

  def findByName(name: String) = inTransaction {
    from(orderTagTable) {
      tag => where(tag.id like name + '%') select (tag.id)
    }.toList
  }

  def insert(order_tag: OrderTag): OrderTag = inTransaction {
    orderTagTable.insert(order_tag)
  }

  def update(order_tag: OrderTag) {
    inTransaction {
      orderTagTable.update(order_tag)
    }
  }

  def delete(id: String) = inTransaction {
    orderTagTable.deleteWhere(order_tag => order_tag.id === id)
  }
}
