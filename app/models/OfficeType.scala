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

case class OfficeType(
                       id: Int,
                       name: String
                       ) extends KeyedEntity[Int]

object OfficeType {

  import Database.{officeTypeTable}

  implicit val officeTypeWrites: Writes[OfficeType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(OfficeType.unapply))

  implicit val officeTypeReads: Reads[OfficeType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](50))
    )(OfficeType.apply _)

  def allQ: Query[OfficeType] = from(officeTypeTable) {
    officeType => select(officeType)
  }

  def findAll: Iterable[OfficeType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(officeTypeTable) {
      officeType => where(officeType.id === id) select (officeType)
    }.headOption
  }

  def insert(officeType: OfficeType): OfficeType = inTransaction {
    officeTypeTable.insert(officeType)
  }

  def update(officeType: OfficeType) {
    inTransaction {
      officeTypeTable.update(officeType)
    }
  }

  def delete(id: Long) = inTransaction {
    officeTypeTable.deleteWhere(officeType => officeType.id === id)
  }
}
