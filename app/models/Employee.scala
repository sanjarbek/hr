package models

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import collection.Iterable

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
  import Database.{employeeTable, familyTable}

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
    from(familyTable) {
      family => where(family.employee_id===employee.id).compute(count)
    }.toLong
  }
}
