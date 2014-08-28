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

case class PositionCategory(
                             id: Int,
                             name: String
                             ) extends KeyedEntity[Int]

object PositionCategory {

  import Database.{positionCategoryTable}

  implicit val positionCategoryWrites: Writes[PositionCategory] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(PositionCategory.unapply))

  implicit val positionCategoryReads: Reads[PositionCategory] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](40))
    )(PositionCategory.apply _)

  def allQ: Query[PositionCategory] = from(positionCategoryTable) {
    positionCategory => select(positionCategory)
  }

  def findAll: Iterable[PositionCategory] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(positionCategoryTable) {
      position => where(position.id === id) select (position)
    }.headOption
  }

  def insert(positionCategory: PositionCategory): PositionCategory = inTransaction {
    positionCategoryTable.insert(positionCategory)
  }

  def update(positionCategory: PositionCategory) {
    inTransaction {
      positionCategoryTable.update(positionCategory)
    }
  }
}
