package models

import java.sql.Timestamp

import org.joda.time.DateTime
import org.squeryl.customtypes.TimestampField
import org.squeryl.{Table, Schema}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

object Database extends Schema {

  //--------------------------------------------------------------------------------------------
  class TimeStamp(t: Timestamp) extends TimestampField(t)

  implicit def jodaToTimeStamp(dateTime: DateTime): TimeStamp = new TimeStamp(new Timestamp(dateTime.getMillis))

  implicit def timeStampToJoda(timeStamp: TimeStamp): DateTime = new DateTime(timeStamp.value.getTime)

  implicit val formatTimestamp = new Format[Timestamp] {

    def writes(ts: Timestamp): JsValue = Json.obj("date" -> ts.toString())

    def reads(ts: JsValue): JsResult[Timestamp] = {
      // something like that... not tested !
      try {
        JsSuccess(Timestamp.valueOf(ts.as[String]))
      } catch {
        case e: IllegalArgumentException => JsError("Unable to parse timestamp")
      }
    }
  }

  //--------------------------------------------------------------------------------------------

  val employeeTable: Table[Employee] = table[Employee]("employees")
  val relationshipTable: Table[Relationship] = table[Relationship]("relationships")
  val relationshipTypeTable: Table[RelationshipType] = table[RelationshipType]("relationship_types")
  val positionTable: Table[Position] = table[Position]("positions")
  val structureTypeTable: Table[StructureType] = table[StructureType]("structure_types")
  val officeTable: Table[Office] = table[Office]("offices")
  val positionTypeTable: Table[PositionType] = table[PositionType]("position_categories")
  val departmentTable: Table[Department] = table[Department]("departments")
  val contractTypeTable: Table[ContractType] = table[ContractType]("contract_types")
  val contractTable: Table[Contract] = table[Contract]("contracts")
  val institutionTable: Table[Institution] = table[Institution]("institutions")
  val educationTable: Table[Education] = table[Education]("educations")
  val passportTable: Table[Passport] = table[Passport]("passports")
  val militaryTable: Table[Military] = table[Military]("military_infos")
  val structureTable: Table[Structure] = table[Structure]("structures")
  val orderTable: Table[Order] = table[Order]("orders")
  val orderTagTable: Table[OrderTag] = table[OrderTag]("order_tags")

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

  on(structureTypeTable) { structure_type => declare {
    structure_type.id is (autoIncremented("structure_types_id_seq"))
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

  on(structureTable) { structure => declare {
    structure.id is (autoIncremented("structures_id_seq"))
  }
  }

  on(orderTable) { order => declare {
    order.id is (autoIncremented("orders_id_seq"))
  }
  }

}