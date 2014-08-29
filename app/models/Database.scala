package models

import org.squeryl.{Table, Schema}
import org.squeryl.PrimitiveTypeMode._

object Database extends Schema {
  val employeeTable: Table[Employee] = table[Employee]("employees")
  val relationshipTable: Table[Relationship] = table[Relationship]("relationships")
  val relationshipTypeTable: Table[RelationshipType] = table[RelationshipType]("relationship_types")
  val positionTable: Table[Position] = table[Position]("positions")
  val officeTypeTable: Table[OfficeType] = table[OfficeType]("office_types")
  val officeTable: Table[Office] = table[Office]("offices")
  val positionCategoryTable: Table[PositionCategory] = table[PositionCategory]("position_categories")
  val departmentTable: Table[Department] = table[Department]("departments")

  on(employeeTable) { emp => declare {
    emp.id is (autoIncremented("employees_id_seq"))
  }
  }

  on(relationshipTable) { fam => declare {
    fam.id is (autoIncremented("relationships_id_seq"))
  }
  }

  on(relationshipTypeTable) { rel_type => declare {
    rel_type.id is (autoIncremented("relationship_types_id_seq"))
  }
  }

  on(positionTable) { pos => declare {
    pos.id is (autoIncremented("positions_id_seq"))
  }
  }

  on(officeTypeTable) { office_type => declare {
    office_type.id is (autoIncremented("office_types_id_seq"))
  }
  }

  on(officeTable) { office => declare {
    office.id is (autoIncremented("offices_id_seq"))
  }
  }

  on(positionCategoryTable) { pos_category => declare {
    pos_category.id is (autoIncremented("position_categories_id_seq"))
  }
  }

  on(departmentTable) { department => declare {
    department.id is (autoIncremented("departments_id_seq"))
  }
  }
}