package models

import java.time.{LocalDateTime, LocalDate}

import MyCustomTypes._
import org.squeryl.{Query, KeyedEntity}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}
import scala.collection.Iterable

case class TransferOrder(
                          id: Long,
                          order_type_id: Int,
                          date_of_order: LocalDate,
                          position_id: Int,
                          contract_type_id: Int,
                          contract_number: Long,
                          employee_id: Long,
                          salary: BigDecimal,
                          calendar_type_id: Int,
                          is_combined_work: Boolean,
                          trial_period_start: Option[LocalDate],
                          trial_period_end: Option[LocalDate],
                          start_date: LocalDate,
                          end_date: Option[LocalDate],
                          override var created_at: LocalDateTime,
                          override var updated_at: LocalDateTime
                          ) extends Entity[Long] {

  override def save = transaction {
    val transferOrder = super.save.asInstanceOf[TransferOrder]
    Employee.findById(transferOrder.employee_id).map { employee =>
      employee.positionHistoryId.map { positionHistoryId =>
        Position.findById(positionHistoryId).map { oldPositionHistory =>
          Structure.findById(oldPositionHistory.position_id).map { oldStructure =>
            //            val leaving_date = new Date(transferOrder.start_date.getTime - (24 * 3600 * 1000))

            // закрытие старой строки истории связывающий должность, сотрудник, и приказ
            //            oldPositionHistory.copy(close_date = Some(leaving_date), transfer_order_id = Some(transferOrder.id)).update
            // закрытие старой строки должности
            Structure.update(oldStructure.copy(positionHistoryId = None))

            // создание новой строки истории связывающий должность, сотрудник, и приказ
            val newPositionHistory = Position(0, None, transferOrder.position_id, transferOrder.employee_id, None, Some(transferOrder.id), transferOrder.start_date, transferOrder.end_date, None, null, null).save

            // сохранение новой ссылки на историю в таблице сотрудника
            employee.copy(positionHistoryId = Some(newPositionHistory.id)).update

            val newStructure = Structure.findById(transferOrder.position_id).get
            Structure.update(newStructure.copy(positionHistoryId = Some(newPositionHistory.id)))
          }
        }
      }
    }
    transferOrder
  }

  def update = inTransaction {
    TransferOrder.findById(this.id).map { transferOrder =>
      val tmp = this.copy(created_at = transferOrder.created_at, updated_at = transferOrder.updated_at)
      tmp.save
    }
  }
}

object TransferOrder {

  import Database.{transferOrderTable, employeeTable, calendarTypeTable, contractTypeTable, structureTable}

  implicit val transferOrderWrites: Writes[TransferOrder] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "order_type_id").write[Int] and
      (JsPath \ "date_of_order").write[LocalDate] and
      (JsPath \ "position_id").write[Int] and
      (JsPath \ "contract_type_id").write[Int] and
      (JsPath \ "contract_number").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "salary").write[BigDecimal] and
      (JsPath \ "calendar_type_id").write[Int] and
      (JsPath \ "is_combined_work").write[Boolean] and
      (JsPath \ "trial_period_start").write[Option[LocalDate]] and
      (JsPath \ "trial_period_end").write[Option[LocalDate]] and
      (JsPath \ "start_date").write[LocalDate] and
      (JsPath \ "end_date").write[Option[LocalDate]] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(TransferOrder.unapply))

  implicit val transferOrderReads: Reads[TransferOrder] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "order_type_id").read[Int] and
      (JsPath \ "date_of_order").read[LocalDate] and
      (JsPath \ "position_id").read[Int] and
      (JsPath \ "contract_type_id").read[Int] and
      (JsPath \ "contract_number").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "salary").read[BigDecimal] and
      (JsPath \ "calendar_type_id").read[Int] and
      (JsPath \ "is_combined_work").read[Boolean] and
      (JsPath \ "trial_period_start").read[Option[LocalDate]] and
      (JsPath \ "trial_period_end").read[Option[LocalDate]] and
      (JsPath \ "start_date").read[LocalDate] and
      (JsPath \ "end_date").read[Option[LocalDate]] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
    )(TransferOrder.apply _)

  def allQ: Query[TransferOrder] = from(transferOrderTable) {
    transferOrder => select(transferOrder)
  }

  def findAll: Iterable[TransferOrder] = inTransaction {
    allQ.toList
  }

  def findFull = inTransaction {
    from(transferOrderTable, employeeTable, contractTypeTable, calendarTypeTable, structureTable) {
      (transferOrder, employee, contractType, calendarType, position) =>
        where(transferOrder.employee_id === employee.id
          and (transferOrder.calendar_type_id === calendarType.id)
          and (transferOrder.position_id === position.id)
          and (transferOrder.contract_type_id === contractType.id)) select(transferOrder, employee, calendarType, contractType, position)
    }.toList
  }

  def findById(id: Long) = inTransaction {
    from(transferOrderTable) {
      transferOrder => where(transferOrder.id === id) select (transferOrder)
    }.headOption
  }
}