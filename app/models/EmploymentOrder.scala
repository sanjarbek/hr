package models

import models.Database.{MyLocalDate, TimeStamp}
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}
import scala.collection.Iterable
import java.util.Date

case class EmploymentOrder(
                            id: Long,
                            order_type_id: Int,
                            date_of_order: Date,
                            position_id: Int,
                            contract_type_id: Int,
                            contract_number: Long,
                            employee_id: Long,
                            salary: BigDecimal,
                            calendar_type_id: Int,
                            is_combined_work: Boolean,
                            trial_period_start: Option[Date],
                            trial_period_end: Option[Date],
                            start_date: Date,
                            end_date: Option[Date],
                            override var created_at: TimeStamp,
                            override var updated_at: TimeStamp
                            ) extends Entity[Long] {

  override def save = transaction {
    val employmentOrder = super.save.asInstanceOf[EmploymentOrder]
    val position = Position(0, Some(employmentOrder.id), employmentOrder.position_id, employmentOrder.employee_id, None, None,
      employmentOrder.start_date, employmentOrder.end_date, None, null, null).save
    Employee.findById(employmentOrder.employee_id).get.copy(positionHistoryId = Some(position.id)).update
    Structure.update(Structure.findById(employmentOrder.position_id).get.copy(positionHistoryId = Some(position.id)))
    employmentOrder
  }

  def update = inTransaction {
    EmploymentOrder.findById(this.id).map { employmentOrder =>
      val tmp = this.copy(created_at = employmentOrder.created_at, updated_at = employmentOrder.updated_at)
      tmp.save
    }
  }
}

object EmploymentOrder {

  import Database.{employmentOrderTable, employeeTable, calendarTypeTable, contractTypeTable, structureTable}

  implicit val employmentOrderWrites: Writes[EmploymentOrder] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_type_id").write[Int] and
      (JsPath \ "date_of_order").write[Date] and
      (JsPath \ "position_id").write[Int] and
      (JsPath \ "contract_type_id").write[Int] and
      (JsPath \ "contract_number").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "salary").write[BigDecimal] and
      (JsPath \ "calendar_type_id").write[Int] and
      (JsPath \ "is_combined_work").write[Boolean] and
      (JsPath \ "trial_period_start").write[Option[Date]] and
      (JsPath \ "trial_period_end").write[Option[Date]] and
      (JsPath \ "start_date").write[Date] and
      (JsPath \ "end_date").write[Option[Date]] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(EmploymentOrder.unapply))

  implicit val employmentOrderReads: Reads[EmploymentOrder] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_type_id").read[Int] and
      (JsPath \ "date_of_order").read[Date] and
      (JsPath \ "position_id").read[Int] and
      (JsPath \ "contract_type_id").read[Int] and
      (JsPath \ "contract_number").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "salary").read[BigDecimal] and
      (JsPath \ "calendar_type_id").read[Int] and
      (JsPath \ "is_combined_work").read[Boolean] and
      (JsPath \ "trial_period_start").read[Option[Date]] and
      (JsPath \ "trial_period_end").read[Option[Date]] and
      (JsPath \ "start_date").read[Date] and
      (JsPath \ "end_date").read[Option[Date]] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(EmploymentOrder.apply _)

  def allQ: Query[EmploymentOrder] = from(employmentOrderTable) {
    employmentOrder => select(employmentOrder)
  }

  def findAll: Iterable[EmploymentOrder] = inTransaction {
    allQ.toList
  }

  def findFull = inTransaction {
    from(employmentOrderTable, employeeTable, contractTypeTable, calendarTypeTable, structureTable) {
      (employmentOrder, employee, contractType, calendarType, position) =>
        where(employmentOrder.employee_id === employee.id
          and (employmentOrder.calendar_type_id === calendarType.id)
          and (employmentOrder.position_id === position.id)
          and (employmentOrder.contract_type_id === contractType.id)) select(employmentOrder, employee, calendarType, contractType, position)
    }.toList
  }

  def findById(id: Long) = inTransaction {
    from(employmentOrderTable) {
      employmentOrder => where(employmentOrder.id === id) select (employmentOrder)
    }.headOption
  }
}