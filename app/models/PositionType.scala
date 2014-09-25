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

case class PositionType(
                         id: Int,
                         name: String
                         ) extends KeyedEntity[Int]

object PositionType {

  import Database.{positionTypeTable}

  implicit val positionTypeWrites: Writes[PositionType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(PositionType.unapply))

  implicit val positionTypeReads: Reads[PositionType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](40))
    )(PositionType.apply _)

  def allQ: Query[PositionType] = from(positionTypeTable) {
    positionCategory => select(positionCategory)
  }

  def findAll: Iterable[PositionType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(positionTypeTable) {
      position => where(position.id === id) select (position)
    }.headOption
  }

  def insert(positionCategory: PositionType): PositionType = inTransaction {
    positionTypeTable.insert(positionCategory)
  }

  def update(positionCategory: PositionType) {
    inTransaction {
      positionTypeTable.update(positionCategory)
    }
  }

  def delete(id: Long) = inTransaction {
    positionTypeTable.deleteWhere(positionCategory => positionCategory.id === id)
  }
}
