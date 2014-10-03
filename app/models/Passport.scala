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

case class Passport(
                     id: Long,
                     employee_id: Long,
                     serial: String,
                     number: String,
                     organ: String,
                     open_date: Date,
                     end_date: Date
                     ) extends KeyedEntity[Long]

object Passport {

  import Database.{passportTable}

  implicit val passportWrites: Writes[Passport] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "serial").write[String] and
      (JsPath \ "number").write[String] and
      (JsPath \ "organ").write[String] and
      (JsPath \ "open_date").write[Date] and
      (JsPath \ "end_date").write[Date]
    )(unlift(Passport.unapply))

  implicit val passportReads: Reads[Passport] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "serial").read[String](minLength[String](2) keepAnd maxLength[String](5)) and
      (JsPath \ "number").read[String](minLength[String](2) keepAnd maxLength[String](10)) and
      (JsPath \ "organ").read[String] and
      (JsPath \ "open_date").read[Date] and
      (JsPath \ "end_date").read[Date]
    )(Passport.apply _)


  def allQ: Query[Passport] = from(passportTable) {
    relationship => select(relationship)
  }

  def findAll: Iterable[Passport] = inTransaction {
    allQ.toList
  }

  def findEmployeePassport(employeeId: Long) = inTransaction {
    from(passportTable) {
      passport => where(passport.employee_id === employeeId) select (passport)
    }.headOption
  }

  def findById(id: Long) = inTransaction {
    from(passportTable) {
      passport => where(passport.id === id) select (passport)
    }.headOption
  }

  def insert(passport: Passport): Passport = inTransaction {
    passportTable.insert(passport)
  }

  def update(passport: Passport) = inTransaction {
    passportTable.update(passport)
  }

  def delete(id: Long) = inTransaction {
    passportTable.deleteWhere(rel => rel.id === id)
  }
}
