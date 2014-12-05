package models

import java.sql.Timestamp
import java.time.{ZoneId, Instant, LocalDate, LocalDateTime}

import org.joda.time.DateTime
import java.util.Date
import org.squeryl.customtypes.{DateField, TimestampField}
import org.squeryl.{Table, Schema}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

object Database extends Schema {

  //--------------------------------------------------------------------------------------------
  class TimeStamp(t: Timestamp) extends TimestampField(t) {
    override def toString = this.value.toString
  }

  implicit def jodaToTimeStamp(dateTime: LocalDateTime): TimeStamp = new TimeStamp(Timestamp.valueOf(dateTime))

  implicit def timeStampToJoda(timeStamp: TimeStamp): LocalDateTime = timeStamp.value.toLocalDateTime


  implicit val formatTimeStamp = new Format[TimeStamp] {
    def writes(ts: TimeStamp): JsValue = Json.toJson(ts.value.toString)

    def reads(ts: JsValue): JsResult[TimeStamp] = {
      try {
        JsSuccess(new TimeStamp(Timestamp.valueOf(ts.as[String])))
      } catch {
        case e: IllegalArgumentException => JsError("Unable to parse timestamp")
      }
    }
  }

  class MyLocalDate(d: Date) extends DateField(d) {
    override def toString = d.toString
  }

  implicit def myLocalDateToLocalDate(date: MyLocalDate): LocalDate = {
    val instant = Instant.ofEpochMilli(date.value.getTime)
    LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate
  }

  implicit def localDateToMyLocalDate(date: LocalDate): MyLocalDate = {
    val instant = date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
    new MyLocalDate(Date.from(instant))
  }

  implicit val formatMyLocalDate = new Format[MyLocalDate] {
    def writes(mld: MyLocalDate): JsValue = {
      val instant = Instant.ofEpochMilli(mld.value.getTime)
      val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate.toString
      Json.toJson(localDate)
    }

    def reads(ts: JsValue): JsResult[MyLocalDate] = {
      try {
        val instant = LocalDate.parse(ts.as[String]).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant
        JsSuccess(new MyLocalDate(Date.from(instant)))
      } catch {
        case e: IllegalArgumentException => JsError("Unable to parse date")
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
  val qualificationTypeTable: Table[QualificationType] = table[QualificationType]("qualification_types")
  val contactInformationTable: Table[ContactInformation] = table[ContactInformation]("contact_informations")
  val calendarTypeTable: Table[CalendarType] = table[CalendarType]("calendar_types")
  val calendarTable: Table[Calendar] = table[Calendar]("calendars")
  val dayTypeTable: Table[DayType] = table[DayType]("day_types")

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

  on(qualificationTypeTable) { qualification => declare {
    qualification.id is (autoIncremented("qualification_types_id_seq"))
  }
  }

  on(contactInformationTable) { contactInformation => declare {
    contactInformation.id is (autoIncremented("contact_informations_id_seq"))
  }
  }

  on(calendarTypeTable) { calendarType => declare {
    calendarType.id is (autoIncremented("calendar_types_id_seq"))
  }
  }

  on(calendarTable) { calendar => declare {
    calendar.id is (autoIncremented("calendars_id_seq"))
  }
  }

  on(dayTypeTable) { dayType => declare {
    dayType.id is (autoIncremented("day_types_id_seq"))
  }
  }

}