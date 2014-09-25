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
  val positionTypeTable: Table[PositionType] = table[PositionType]("position_categories")
  val departmentTable: Table[Department] = table[Department]("departments")
  val contractTypeTable: Table[ContractType] = table[ContractType]("contract_types")
  val contractTable: Table[Contract] = table[Contract]("contracts")
  val institutionTable: Table[Institution] = table[Institution]("institutions")
  val educationTable: Table[Education] = table[Education]("educations")
  val passportTable: Table[Passport] = table[Passport]("passports")
  val militaryTable: Table[Military] = table[Military]("military_infos")

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

  on(positionTypeTable) { pos_category => declare {
    pos_category.id is (autoIncremented("position_categories_id_seq"))
  }
  }

  on(departmentTable) { department => declare {
    department.id is (autoIncremented("departments_id_seq"))
  }
  }

  on(contractTypeTable) { contract_type => declare {
    contract_type.id is (autoIncremented("contract_types_id_seq"))
  }
  }

  on(contractTable) { contract => declare {
    contract.id is (autoIncremented("contracts_id_seq"))
  }
  }

  on(institutionTable) { institution => declare {
    institution.id is (autoIncremented("institutions_id_seq"))
  }
  }

  on(educationTable) { education => declare {
    education.id is (autoIncremented("educations_id_seq"))
  }
  }

  on(passportTable) { passport => declare {
    passport.id is (autoIncremented("passports_id_seq"))
  }
  }

  on(militaryTable) { military => declare {
    military.id is (autoIncremented("military_infos_id_seq"))
  }
  }
}