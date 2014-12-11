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
                     category: Option[String],
                     military_rank: Option[String],
                     structure: Option[String],
                     full_code: Option[String],
                     validity_category: Option[String],
                     commissariat: String,
                     removal_mark: Option[String]
                     ) extends KeyedEntity[Long]

object Military {

  import Database.{militaryTable}

  implicit val militaryWrites: Writes[Military] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "category").write[Option[String]] and
      (JsPath \ "military_rank").write[Option[String]] and
      (JsPath \ "structure").write[Option[String]] and
      (JsPath \ "full_code").write[Option[String]] and
      (JsPath \ "validity_category").write[Option[String]] and
      (JsPath \ "commissariat").write[String] and
      (JsPath \ "removal_mark").write[Option[String]]
    )(unlift(Military.unapply))

  implicit val militaryReads: Reads[Military] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "category").read[Option[String]] and
      (JsPath \ "military_rank").read[Option[String]] and
      (JsPath \ "structure").read[Option[String]] and
      (JsPath \ "full_code").read[Option[String]] and
      (JsPath \ "validity_category").read[Option[String]] and
      (JsPath \ "commissariat").read[String] and
      (JsPath \ "removal_mark").read[Option[String]]
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
