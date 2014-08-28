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

case class Office(
                         id: Int,
                         parent_id: Int,
                         type_id: Int,
                         name: String,
                         phone: String,
                         email: String,
                         fax: String,
                         address: String
                         ) extends KeyedEntity[Int]

object Office {
  import Database.{officeTable}

  implicit val officeWrites: Writes[Office] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "parent_id").write[Int] and
      (JsPath \ "type_id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "phone").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "fax").write[String] and
      (JsPath \ "address").write[String]
    )(unlift(Office.unapply))

  implicit val officeReads: Reads[Office] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "parent_id").read[Int] and
      (JsPath \ "type_id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](20)) and
      (JsPath \ "phone").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "fax").read[String] and
      (JsPath \ "address").read[String]
    )(Office.apply _)


  def allQ: Query[Office] = from(officeTable) {
    office => select(office)
  }

  def findAll: Iterable[Office] = inTransaction{
    allQ.toList
  }

  def findById(id: Long) = inTransaction{
    from(officeTable) {
      office => where(office.id === id) select(office)
    }.headOption
  }

  def insert(office: Office): Office = inTransaction{
    officeTable.insert(office)
  }

  def update(office: Office) {
    inTransaction{ officeTable.update(office)}
  }
}
