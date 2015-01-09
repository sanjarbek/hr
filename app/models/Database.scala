package models

import java.sql.Timestamp
import java.sql.Date
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId}

import org.joda.time.DateTime
import org.squeryl.dsl._

import org.squeryl.{Table, Schema}
import play.api.libs.json._


object MyCustomTypes extends org.squeryl.PrimitiveTypeMode {

  // optionally define custom types :
  implicit val jodaTimeTEF = new NonPrimitiveJdbcMapper[Timestamp, DateTime, TTimestamp](timestampTEF, this) {
    def convertFromJdbc(t: Timestamp) = new DateTime(t)

    def convertToJdbc(t: DateTime) = new Timestamp(t.getMillis())
  }
  implicit val optionJodaTimeTEF = new TypedExpressionFactory[Option[DateTime], TOptionTimestamp]
    with DeOptionizer[Timestamp, DateTime, TTimestamp, Option[DateTime], TOptionTimestamp] {
    val deOptionizer = jodaTimeTEF
  }

  implicit def jodaTimeToTE(s: DateTime) = jodaTimeTEF.create(s)

  implicit def optionJodaTimeToTE(s: Option[DateTime]) = optionJodaTimeTEF.create(s)

  // ------------------------------------------------------------------------------------------------------------------

  // --------------- java8 LocalDate to sql Date ------------------------------
  implicit val localDateTEF = new NonPrimitiveJdbcMapper[Date, LocalDate, TDate](sqlDateTEF, this) {
    def convertFromJdbc(d: Date) = if (d == null) null else d.toLocalDate

    def convertToJdbc(d: LocalDate) = Date.valueOf(d)
  }

  implicit val optionLocalDateTEF =
    new TypedExpressionFactory[Option[LocalDate], TOptionDate] with DeOptionizer[Date, LocalDate, TDate, Option[LocalDate], TOptionDate] {
      val deOptionizer = localDateTEF
    }

  implicit def localDateToTE(s: LocalDate) = localDateTEF.create(s)
  implicit def optionLocalDateToTE(s: Option[LocalDate]) = optionLocalDateTEF.create(s)

  implicit val formatLocalDate = new Format[LocalDate] {
    def writes(ld: LocalDate): JsValue = Json.toJson(ld.toString)
    def reads(js: JsValue): JsResult[LocalDate] = {
      try {
        JsSuccess(LocalDate.parse(js.as[String]))
      } catch {
        case e: IllegalArgumentException => JsError("Unable to parse localdate")
      }
    }
  }
  // -----------------------------------------------------------------------------------------------------------------

  // --------------- java8 LocalDateTime to Timestamp ------------------------------
  implicit val localDateTimeTEF = new NonPrimitiveJdbcMapper[Timestamp, LocalDateTime, TTimestamp](timestampTEF, this) {
    def convertFromJdbc(t: Timestamp) = if (t == null) null else t.toLocalDateTime
    def convertToJdbc(d: LocalDateTime) = Timestamp.from(d.atZone(ZoneId.systemDefault()).toInstant)
  }

  implicit val optionLocalDateTimeTEF =
    new TypedExpressionFactory[Option[LocalDateTime], TOptionTimestamp] with DeOptionizer[Timestamp, LocalDateTime, TTimestamp, Option[LocalDateTime], TOptionTimestamp] {
      val deOptionizer = localDateTimeTEF
    }

  implicit def localDateTimeToTE(s: LocalDateTime) = localDateTimeTEF.create(s)

  implicit def optionLocalDateTimeToTE(s: Option[LocalDateTime]) = optionLocalDateTimeTEF.create(s)

  implicit val formatLocalDateTime = new Format[LocalDateTime] {
    def writes(ld: LocalDateTime): JsValue = Json.toJson(ld.toString)

    def reads(js: JsValue): JsResult[LocalDateTime] = {
      try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
        //        JsSuccess(LocalDateTime.parse(js.as[String], DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        JsSuccess(LocalDateTime.parse(js.as[String], formatter))
      } catch {
        case e: IllegalArgumentException => JsError("Unable to parse localdate")
      }
    }
  }
  // -----------------------------------------------------------------------------------------------------------------

}


import models.MyCustomTypes._

object Database extends Schema {

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
  val relationshipStatusTable: Table[RelationshipStatus] = table[RelationshipStatus]("relationship_statuses")
  val nationalityTable: Table[Nationality] = table[Nationality]("nationalities")
  val seminarTable: Table[Seminar] = table[Seminar]("seminars")
  val employmentOrderTable: Table[EmploymentOrder] = table[EmploymentOrder]("employment_orders")
  val dismissalOrderTable: Table[DismissalOrder] = table[DismissalOrder]("dismissal_orders")
  val transferOrderTable: Table[TransferOrder] = table[TransferOrder]("transfer_orders")
  val leavingReasonTable: Table[LeavingReason] = table[LeavingReason]("leaving_reasons")
  val workingSheetDayTable: Table[WorkingSheetDay] = table[WorkingSheetDay]("sheet_working_days")
  val empTestTable: Table[EmpTest] = table[EmpTest]("emptest")

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

  on(nationalityTable) { nationality => declare {
    nationality.id is (autoIncremented("nationalities_id_seq"))
  }
  }

  on(relationshipStatusTable) { relationshipStatus => declare {
    relationshipStatus.id is (autoIncremented("relationship_statuses_id_seq"))
  }
  }

  on(seminarTable) { seminar => declare {
    seminar.id is (autoIncremented("seminars_id_seq"))
  }
  }

  on(employmentOrderTable) { employmentOrder => declare {
    employmentOrder.id is (autoIncremented("orders_id_seq"))
    //    employmentOrder.contract_number is (autoIncremented("employment_orders_contract_number_seq"))
  }
  }

  on(dismissalOrderTable) { dismissalOrder => declare {
    dismissalOrder.id is (autoIncremented("orders_id_seq"))
  }
  }

  on(transferOrderTable) { transferOrder => declare {
    transferOrder.id is (autoIncremented("orders_id_seq"))
  }
  }

  on(leavingReasonTable) { leavingReason => declare(
    leavingReason.id is (autoIncremented("leaving_reasons_id_seq")))
  }

  on(workingSheetDayTable) { workingSheetDay => declare(
    workingSheetDay.id is (autoIncremented("sheet_working_days_id_seq")))
  }

  on(empTestTable) { empTest => declare(
    empTest.id is (autoIncremented("emptest_id_seq")))
  }

}