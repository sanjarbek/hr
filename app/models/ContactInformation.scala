package models

import java.time.LocalDateTime

import models.MyCustomTypes._
import org.squeryl.Query
import collection.Iterable
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ContactInformation(
                               id: Long,
                               employee_id: Long,
                               home_address: Option[String],
                               living_address: String,
                               email: Option[String],
                               home_phone: Option[String],
                               mobile_phone: String,
                               override var created_at: LocalDateTime,
                               override var updated_at: LocalDateTime
                               ) extends Entity[Long] {

  override def save = inTransaction {
    super.save.asInstanceOf[ContactInformation]
  }

  def update = inTransaction {
    ContactInformation.findById(this.id).map { contactInformation =>
      val tmp = this.copy(created_at = contactInformation.created_at, updated_at = contactInformation.updated_at)
      tmp.save
    }
  }
}

object ContactInformation {

  import Database.{contactInformationTable}

  implicit val contactInformationWrites: Writes[ContactInformation] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "home_address").write[Option[String]] and
      (JsPath \ "living_address").write[String] and
      (JsPath \ "email").write[Option[String]] and
      (JsPath \ "home_phone").write[Option[String]] and
      (JsPath \ "mobile_phone").write[String] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(ContactInformation.unapply))

  implicit val contactInformationReads: Reads[ContactInformation] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "home_address").read[Option[String]] and
      (JsPath \ "living_address").read[String] and
      (JsPath \ "email").read[Option[String]] and
      (JsPath \ "home_phone").read[Option[String]] and
      (JsPath \ "mobile_phone").read[String] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
    )(ContactInformation.apply _)

  def allQ: Query[ContactInformation] = from(contactInformationTable) {
    contactInformation => select(contactInformation)
  }

  def findAll: Iterable[ContactInformation] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(contactInformationTable) {
      contactInformation => where(contactInformation.id === id) select (contactInformation)
    }.headOption
  }

  def findEmployeeInformation(id: Long) = inTransaction {
    from(contactInformationTable) {
      contactInformation => where(contactInformation.employee_id === id) select (contactInformation)
    }.headOption
  }

  def delete(id: Long) = inTransaction {
    contactInformationTable.deleteWhere { contactInformation =>
      (contactInformation.id === id)
    }
  }
}
