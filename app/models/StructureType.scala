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

case class StructureType(
                          id: Int,
                          name: String,
                          has_children: Boolean
                          ) extends KeyedEntity[Int]

object StructureType {

  import Database.{structureTypeTable}

  implicit val structureTypeWrites: Writes[StructureType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "has_children").write[Boolean]
    )(unlift(StructureType.unapply))

  implicit val structureTypeReads: Reads[StructureType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](50)) and
      (JsPath \ "has_children").read[Boolean]
    )(StructureType.apply _)

  def allQ: Query[StructureType] = from(structureTypeTable) {
    structureType => select(structureType)
  }

  def findAll: Iterable[StructureType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(structureTypeTable) {
      structureType => where(structureType.id === id) select (structureType)
    }.headOption
  }

  def insert(structureType: StructureType): StructureType = inTransaction {
    structureTypeTable.insert(structureType)
  }

  def update(structureType: StructureType) {
    inTransaction {
      structureTypeTable.update(structureType)
    }
  }

  def delete(id: Long) = inTransaction {
    structureTypeTable.deleteWhere(structureType => structureType.id === id)
  }
}
