package models

import org.squeryl.{Table, Schema}
import org.squeryl.PrimitiveTypeMode._

object Database extends Schema {
  val employeeTable: Table[Employee] =
    table[Employee]("employees")
  val familyTable: Table[Family] =
    table[Family]("family")

  on(employeeTable) { emp => declare {
    emp.id is(autoIncremented("employees_id_seq"))
  }}

  on(familyTable) { fam => declare {
    fam.id is(autoIncremented("family_id_seq"))
  }}
}