package models

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import collection.Iterable
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Employee(
  id: Long,
  surname: String,
  firstname: String,
  lastname: String,
  birthday: Date,
  citizenship: String,
  insurance_number: String,
  tax_number: String,
  home_phone: String,
  mobile_phone: String,
  email: String ) extends KeyedEntity[Long]

object Employee {
  import Database.{employeeTable, relationshipTable}

  implicit val employeeWrites: Writes[Employee] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "surname").write[String] and
      (JsPath \ "firstname").write[String] and
      (JsPath \ "lastname").write[String] and
      (JsPath \ "birthday").write[Date] and
      (JsPath \ "citizenship").write[String] and
      (JsPath \ "insurance_number").write[String] and
      (JsPath \ "tax_number").write[String] and
      (JsPath \ "home_phone").write[String] and
      (JsPath \ "mobile_phone").write[String] and
      (JsPath \ "email").write[String]
    )(unlift(Employee.unapply))

  implicit val employeeReads: Reads[Employee] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "surname").read[String] and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "birthday").read[Date] and
      (JsPath \ "citizenship").read[String] and
      (JsPath \ "insurance_number").read[String] and
      (JsPath \ "tax_number").read[String] and
      (JsPath \ "home_phone").read[String] and
      (JsPath \ "mobile_phone").read[String] and
      (JsPath \ "email").read[String]
    )(Employee.apply _)

  def allQ: Query[Employee] = from(employeeTable) {
    employee => select(employee)
  }

  def findAll: Iterable[Employee] = inTransaction{
    allQ.toList
  }

  def findById(id: Long) = inTransaction{
    from(employeeTable) {
      employee => where(employee.id === id) select(employee)
    }.headOption
  }

  def insert(employee: Employee): Employee = inTransaction{
    employeeTable.insert(employee)
  }

  def update(employee: Employee) {
    inTransaction{ employeeTable.update(employee)}
  }

  def listOfFamily(employee: Employee)  = inTransaction{
    from(relationshipTable) {
      family => where(family.employee_id===employee.id).compute(count)
    }.toLong
  }

  def passport(id: Long) = {
    val passports = Passport.findEmployeePassport(id)(0)
    passports
  }
}
