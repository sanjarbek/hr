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

case class Department(
                       id: Int,
                       parent_id: Option[Int],
                       office_id: Int,
                       name: String
                       ) extends KeyedEntity[Int]

object Department {

  import Database.{departmentTable}

  implicit val officeWrites: Writes[Department] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "parent_id").write[Option[Int]] and
      (JsPath \ "office_id").write[Int] and
      (JsPath \ "name").write[String]
    )(unlift(Department.unapply))

  implicit val officeReads: Reads[Department] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "parent_id").read[Option[Int]] and
      (JsPath \ "office_id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](50))
    )(Department.apply _)


  def allQ: Query[Department] = from(departmentTable) {
    office => select(office)
  }

  def findAll: Iterable[Department] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(departmentTable) {
      department => where(department.id === id) select (department)
    }.headOption
  }

  def insert(department: Department): Department = inTransaction {
    departmentTable.insert(department)
  }

  def update(department: Department) {
    inTransaction {
      departmentTable.update(department)
    }
  }
}
