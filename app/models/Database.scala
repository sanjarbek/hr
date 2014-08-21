package models

import org.squeryl.{Table, Schema}
import org.squeryl.PrimitiveTypeMode._

object Database extends Schema {
  val employeeTable: Table[Employee] =
    table[Employee]("employees")
  val relationshipTable: Table[Relationship] =
    table[Relationship]("relationships")

  on(employeeTable) { emp => declare {
    emp.id is(autoIncremented("employees_id_seq"))
  }}

  on(relationshipTable) { fam => declare {
    fam.id is(autoIncremented("relationships_id_seq"))
  }}
}