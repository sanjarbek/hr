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

case class RelationshipType(
                             id: Int,
                             name: String
                             ) extends KeyedEntity[Int]

object RelationshipType {

  import Database.{relationshipTypeTable}

  implicit val relationshipTypeWrites: Writes[RelationshipType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(RelationshipType.unapply))

  implicit val relationshipTypeReads: Reads[RelationshipType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](20))
    )(RelationshipType.apply _)

  def allQ: Query[RelationshipType] = from(relationshipTypeTable) {
    relationshipType => select(relationshipType)
  }

  def findAll: Iterable[RelationshipType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(relationshipTypeTable) {
      relationshipType => where(relationshipType.id === id) select (relationshipType)
    }.headOption
  }

  def insert(relationshipType: RelationshipType): RelationshipType = inTransaction {
    relationshipTypeTable.insert(relationshipType)
  }

  def update(relationshipType: RelationshipType) {
    inTransaction {
      relationshipTypeTable.update(relationshipType)
    }
  }

  def delete(id: Long) = inTransaction {
    relationshipTypeTable.deleteWhere(relationshipType => relationshipType.id === id)
  }
}
