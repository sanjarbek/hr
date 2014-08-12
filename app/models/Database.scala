package models

import org.squeryl.{Table, Schema}
import org.squeryl.PrimitiveTypeMode._

object Database extends Schema {
  val employeeTable: Table[Employee] =
    table[Employee]("employees")

  on(employeeTable) { emp => declare {
    emp.id is(autoIncremented("employees_id_seq"))
  }}
}