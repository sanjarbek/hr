package models

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable

case class Contract(
                     id: Long,
                     position_id: Int,
                     contract_type: Int,
                     employee_id: Long,
                     salary: BigDecimal,
                     working_time_type: Int,
                     trial_period_open: Option[Date],
                     trial_period_end: Option[Date],
                     open_date: Date,
                     end_date: Date,
                     close_date: Option[Date],
                     status: Int
                     ) extends KeyedEntity[Long]

object Contract {

  import Database.{contractTable}

  implicit val contractWrites: Writes[Contract] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "position_id").write[Int] and
      (JsPath \ "contract_type").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "salary").write[BigDecimal] and
      (JsPath \ "working_time_type").write[Int] and
      (JsPath \ "trial_period_open").write[Option[Date]] and
      (JsPath \ "trial_period_end").write[Option[Date]] and
      (JsPath \ "open_date").write[Date] and
      (JsPath \ "end_date").write[Date] and
      (JsPath \ "close_date").write[Option[Date]] and
      (JsPath \ "status").write[Int]
    )(unlift(Contract.unapply))

  implicit val contractReads: Reads[Contract] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "position_id").read[Int] and
      (JsPath \ "contract_type").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "salary").read[BigDecimal] and
      (JsPath \ "working_time_type").read[Int] and
      (JsPath \ "trial_period_open").read[Option[Date]] and
      (JsPath \ "trial_period_end").read[Option[Date]] and
      (JsPath \ "open_date").read[Date] and
      (JsPath \ "end_date").read[Date] and
      (JsPath \ "close_date").read[Option[Date]] and
      (JsPath \ "status").read[Int]
    )(Contract.apply _)

  def allQ: Query[Contract] = from(contractTable) {
    contract => select(contract)
  }

  def findAll: Iterable[Contract] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(contractTable) {
      contract => where(contract.id === id) select (contract)
    }.headOption
  }

  def insert(contract: Contract): Contract = inTransaction {
    contractTable.insert(contract)
  }

  def update(contract: Contract) {
    inTransaction {
      contractTable.update(contract)
    }
  }

  def delete(id: Long) = inTransaction {
    contractTable.deleteWhere(contract => contract.id === id)
  }
}
