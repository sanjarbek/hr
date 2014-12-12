package models

import models.Database.{MyLocalDate, TimeStamp}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Query
import org.squeryl.annotations.Column
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

import scala.collection.Iterable

case class Seminar(
                    id: Long,
                    employee_id: Long,
                    topic: String,
                    organizer: String,
                    event_date: MyLocalDate,
                    @Column("has_certificate") val hasCertificate: Boolean,
                    override var created_at: TimeStamp,
                    override var updated_at: TimeStamp
                    ) extends Entity[Long] {
  override def save = inTransaction {
    super.save.asInstanceOf[Seminar]
  }

  def update = inTransaction {
    Seminar.findById(this.id).map { seminar =>
      val tmp = this.copy(created_at = seminar.created_at, updated_at = seminar.updated_at)
      tmp.save
    }
  }
}

object Seminar {

  import Database.{seminarTable}

  implicit val seminarWrites: Writes[Seminar] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "topic").write[String] and
      (JsPath \ "organizer").write[String] and
      (JsPath \ "event_date").write[MyLocalDate] and
      (JsPath \ "hasCertificate").write[Boolean] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(Seminar.unapply))

  implicit val seminarReads: Reads[Seminar] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "topic").read[String] and
      (JsPath \ "organizer").read[String] and
      (JsPath \ "event_date").read[MyLocalDate] and
      (JsPath \ "hasCertificate").read[Boolean] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(Seminar.apply _)

  def allQ: Query[Seminar] = from(seminarTable) {
    certificate => select(certificate)
  }

  def findAll: Iterable[Seminar] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(seminarTable) {
      seminar => where(seminar.id === id) select (seminar)
    }.headOption
  }

  def findEmployeeSeminars(employeeId: Long) = inTransaction {
    from(seminarTable) {
      seminar => where(seminar.employee_id === employeeId) select (seminar)
    }.toList
  }

  def delete(id: Long) = inTransaction {
    seminarTable.delete(id)
  }
}
