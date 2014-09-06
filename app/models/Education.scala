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

case class Education(
                      id: Long,
                      institution_id: Int,
                      employee_id: Long,
                      start_date: Date,
                      end_date: Date,
                      serialnumber: String,
                      speciality: String,
                      qualification: String
                      ) extends KeyedEntity[Long]

object Education {

  import Database.{educationTable}

  implicit val educationWrites: Writes[Education] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "institution_id").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "start_date").write[Date] and
      (JsPath \ "end_date").write[Date] and
      (JsPath \ "serialnumber").write[String] and
      (JsPath \ "speciality").write[String] and
      (JsPath \ "qualification").write[String]
    )(unlift(Education.unapply))

  implicit val educationReads: Reads[Education] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "institution_id").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "start_date").read[Date] and
      (JsPath \ "end_date").read[Date] and
      (JsPath \ "serialnumber").read[String] and
      (JsPath \ "speciality").read[String] and
      (JsPath \ "qualification").read[String]
    )(Education.apply _)

  def allQ: Query[Education] = from(educationTable) {
    education => select(education)
  }

  def findAll: Iterable[Education] = inTransaction {
    allQ.toList
  }


  def findEmployeeEducations(employeeId: Long) = inTransaction {
    from(allQ) { education =>
      where(education.employee_id === employeeId) select (education)
    }.toList
  }


  def findById(id: Long) = inTransaction {
    from(educationTable) {
      education => where(education.id === id) select (education)
    }.headOption
  }

  def insert(education: Education): Education = inTransaction {
    educationTable.insert(education)
  }

  def update(education: Education) {
    inTransaction {
      educationTable.update(education)
    }
  }
}
