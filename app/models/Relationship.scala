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

case class Relationship(
                     id: Long,
                     employee_id: Long,
                     degree: Int,
                     surname: String,
                     firstname: String,
                     lastname: String,
                     birthday: Date
) extends KeyedEntity[Long]

object Relationship {
  import Database.{relationshipTable}

  implicit val relationshipWrites: Writes[Relationship] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "degree").write[Int] and
      (JsPath \ "surname").write[String] and
      (JsPath \ "firstname").write[String] and
      (JsPath \ "lastname").write[String] and
      (JsPath \ "birthday").write[Date]
    )(unlift(Relationship.unapply))

  implicit val relationshipReads: Reads[Relationship] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "degree").read[Int] and
      (JsPath \ "surname").read[String](minLength[String](2) keepAnd maxLength[String](20)) and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "birthday").read[Date]
    )(Relationship.apply _)


  def allQ: Query[Relationship] = from(relationshipTable) {
    relationship => select(relationship)
  }

  def findAll: Iterable[Relationship] = inTransaction{
    allQ.toList
  }

  def findEmployeeFamily(employeeId: Long) = inTransaction{
    from(allQ) { relationship =>
      where(relationship.employee_id===employeeId) select(relationship)
    }.toList
  }

  def findById(id: Long) = inTransaction{
    from(relationshipTable) {
      relationship => where(relationship.id === id) select(relationship)
    }.headOption
  }

  def insert(relationship: Relationship): Relationship = inTransaction{
    relationshipTable.insert(relationship)
  }

  def update(relationship: Relationship) = inTransaction {
    relationshipTable.update(relationship)
  }

  def delete(id: Long) = inTransaction {
    relationshipTable.deleteWhere(rel => rel.id === id)
  }
}
