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

case class Military(
                     id: Long,
                     employee_id: Long,
                     category: String,
                     military_rank: String,
                     structure: String,
                     full_code: String,
                     validity_category: String,
                     commissariat: String,
                     removal_mark: String
                     ) extends KeyedEntity[Long]

object Military {

  import Database.{militaryTable}

  implicit val militaryWrites: Writes[Military] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "category").write[String] and
      (JsPath \ "military_rank").write[String] and
      (JsPath \ "structure").write[String] and
      (JsPath \ "full_code").write[String] and
      (JsPath \ "validity_category").write[String] and
      (JsPath \ "commissariat").write[String] and
      (JsPath \ "removal_mark").write[String]
    )(unlift(Military.unapply))

  implicit val militaryReads: Reads[Military] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "category").read[String] and
      (JsPath \ "military_rank").read[String] and
      (JsPath \ "structure").read[String] and
      (JsPath \ "full_code").read[String] and
      (JsPath \ "validity_category").read[String] and
      (JsPath \ "commissariat").read[String] and
      (JsPath \ "removal_mark").read[String]
    )(Military.apply _)

  def allQ: Query[Military] = from(militaryTable) {
    military => select(military)
  }

  def findAll: Iterable[Military] = inTransaction {
    allQ.toList
  }

  def findEmployeeMilitary(employeeId: Long) = inTransaction {
    from(allQ) { military =>
      where(military.employee_id === employeeId) select (military)
    }.headOption
  }

  def findById(id: Long) = inTransaction {
    from(militaryTable) {
      military => where(military.id === id) select (military)
    }.headOption
  }

  def insert(military: Military): Military = inTransaction {
    militaryTable.insert(military)
  }

  def update(military: Military) = inTransaction {
    militaryTable.update(military)
  }

  def delete(id: Long) = inTransaction {
    militaryTable.deleteWhere(rel => rel.id === id)
  }
}
