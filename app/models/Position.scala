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

case class Position(
                     id: Int,
                     category_id: Int,
                     name: String
                     ) extends KeyedEntity[Int]

object Position {

  import Database.{positionTable}

  implicit val positionWrites: Writes[Position] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "category_id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(Position.unapply))

  implicit val positionReads: Reads[Position] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "category_id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](20))
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

  def insert(position: Position): Position = inTransaction {
    positionTable.insert(position)
  }

  def update(position: Position) {
    inTransaction {
      positionTable.update(position)
    }
  }
}
