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

case class Institution(
                        id: Int,
                        longname: String,
                        shortname: String
                        ) extends KeyedEntity[Int]

object Institution {

  import Database.{institutionTable}

  implicit val institutionWrites: Writes[Institution] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "shortname").write[String] and
      (JsPath \ "longname").write[String]
    )(unlift(Institution.unapply))

  implicit val institutionReads: Reads[Institution] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "shortname").read[String](minLength[String](2) keepAnd maxLength[String](50)) and
      (JsPath \ "longname").read[String](minLength[String](2) keepAnd maxLength[String](50))
    )(Institution.apply _)

  def allQ: Query[Institution] = from(institutionTable) {
    institution => select(institution)
  }

  def findAll: Iterable[Institution] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(institutionTable) {
      institution => where(institution.id === id) select (institution)
    }.headOption
  }

  def insert(institution: Institution): Institution = inTransaction {
    institutionTable.insert(institution)
  }

  def update(institution: Institution) {
    inTransaction {
      institutionTable.update(institution)
    }
  }
}
