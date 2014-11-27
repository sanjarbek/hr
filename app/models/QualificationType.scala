package models

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable

case class QualificationType(
                              id: Int,
                              name: String
                              ) extends KeyedEntity[Int]

object QualificationType {

  import Database.{qualificationTypeTable}

  implicit val qualifacationTypeWrites: Writes[QualificationType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(QualificationType.unapply))

  implicit val qualifacationTypeReads: Reads[QualificationType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](50))
    )(QualificationType.apply _)

  def allQ: Query[QualificationType] = from(qualificationTypeTable) {
    qualification => select(qualification)
  }

  def findAll: Iterable[QualificationType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(qualificationTypeTable) {
      qualification => where(qualification.id === id) select (qualification)
    }.headOption
  }

  def insert(qualification: QualificationType): QualificationType = inTransaction {
    qualificationTypeTable.insert(qualification)
  }

  def update(qualification: QualificationType) {
    inTransaction {
      qualificationTypeTable.update(qualification)
    }
  }

  def delete(id: Long) = inTransaction {
    qualificationTypeTable.deleteWhere(qualification => qualification.id === id)
  }
}