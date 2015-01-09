package models

import java.time.{LocalDateTime, LocalDate}
import MyCustomTypes._

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
                    event_date: LocalDate,
                    @Column("has_certificate") val hasCertificate: Boolean,
                    override var created_at: LocalDateTime,
                    override var updated_at: LocalDateTime
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
      (JsPath \ "event_date").write[LocalDate] and
      (JsPath \ "hasCertificate").write[Boolean] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(Seminar.unapply))

  implicit val seminarReads: Reads[Seminar] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "topic").read[String] and
      (JsPath \ "organizer").read[String] and
      (JsPath \ "event_date").read[LocalDate] and
      (JsPath \ "hasCertificate").read[Boolean] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
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
