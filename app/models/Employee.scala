package models

import java.sql.Timestamp
import java.util.Date

import models.Database.{TimeStamp, formatTimeStamp}
import org.joda.time.DateTime
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.{Column, ColumnBase}
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.Logger
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
                     sex: Boolean,
                     override var created_at: TimeStamp,
                     override var updated_at: TimeStamp
                     ) extends Entity[Long] {
  override def save = inTransaction {
    super.save.asInstanceOf[Employee]
  }

  def update = inTransaction {
    Employee.findById(this.id).map { employee =>
      val tmp = this.copy(created_at = employee.created_at, updated_at = employee.updated_at)
      tmp.save
    }
  }
}

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
      (JsPath \ "sex").write[Boolean] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
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
      (JsPath \ "sex").read[Boolean] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(Employee.apply _)

  def allQ: Query[Employee] = from(employeeTable) {
    employee => select(employee)
  }

  def findAll: Iterable[Employee] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(employeeTable) {
      employee => where(employee.id === id) select (employee)
    }.headOption
  }

  def insert(employee: Employee): Employee = inTransaction {
    employeeTable.insert(employee)
  }

  def update(employee: Employee) = inTransaction {
    employeeTable.update(employee)
  }

  def listOfFamily(employee: Employee) = inTransaction {
    from(relationshipTable) {
      family => where(family.employee_id === employee.id).compute(count)
    }.toLong
  }
}
