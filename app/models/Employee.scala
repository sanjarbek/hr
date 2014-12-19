package models

import models.Database.{MyLocalDate, TimeStamp, formatTimeStamp, formatMyLocalDate}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.{Column, ColumnBase}
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.Logger
import collection.Iterable
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Employee(
                     id: Long,
                     surname: String,
                     firstname: String,
                     lastname: String,
                     birthday: MyLocalDate,
                     citizenship: String,
                     insurance_number: String,
                     tax_number: String,
                     sex: Boolean,
                     @Column("relationship_status_id") val relationshipStatus: Int,
                     @Column("nationality_id") val nationalityId: Int,
                     @Column("employment_order_id") val employmentOrderId: Option[Long],
                     override var created_at: TimeStamp,
                     override var updated_at: TimeStamp
                     ) extends Entity[Long] {
  override def save = inTransaction {
    super.save.asInstanceOf[Employee]
  }

  def update = inTransaction {
    Employee.findById(this.id).map { employee =>
      val tmp = this.copy(created_at = employee.created_at, updated_at = employee.updated_at)
      tmp.save
    }
  }
}

object Employee {

  import Database.{employeeTable, relationshipTable}

  implicit val employeeWrites: Writes[Employee] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "surname").write[String] and
      (JsPath \ "firstname").write[String] and
      (JsPath \ "lastname").write[String] and
      (JsPath \ "birthday").write[MyLocalDate] and
      (JsPath \ "citizenship").write[String] and
      (JsPath \ "insurance_number").write[String] and
      (JsPath \ "tax_number").write[String] and
      (JsPath \ "sex").write[Boolean] and
      (JsPath \ "relationshipStatus").write[Int] and
      (JsPath \ "nationalityId").write[Int] and
      (JsPath \ "employmentOrderId").write[Option[Long]] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(Employee.unapply))

  implicit val employeeReads: Reads[Employee] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "surname").read[String] and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "birthday").read[MyLocalDate] and
      (JsPath \ "citizenship").read[String] and
      (JsPath \ "insurance_number").read[String] and
      (JsPath \ "tax_number").read[String] and
      (JsPath \ "sex").read[Boolean] and
      (JsPath \ "relationshipStatus").read[Int] and
      (JsPath \ "nationalityId").read[Int] and
      (JsPath \ "employmentOrderId").read[Option[Long]] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(Employee.apply _)

  def allQ: Query[Employee] = from(employeeTable) {
    employee => select(employee)
  }

  def findAll: Iterable[Employee] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(employeeTable) {
      employee => where(employee.id === id) select (employee)
    }.headOption
  }

  def insert(employee: Employee): Employee = inTransaction {
    employeeTable.insert(employee)
  }

  def update(employee: Employee) = inTransaction {
    employeeTable.update(employee)
  }

  def listOfFamily(employee: Employee) = inTransaction {
    from(relationshipTable) {
      family => where(family.employee_id === employee.id).compute(count)
    }.toLong
  }

  def findAllUnemployed = inTransaction {
    from(employeeTable) {
      employee => where(employee.employmentOrderId.isNull) select (employee)
    }.toList
  }

  def findAllEmployed = inTransaction {
    from(employeeTable) { employee =>
      where(employee.employmentOrderId.isNotNull) select (employee)
    }.toList
  }

}


case class Nationality(
                        id: Int,
                        name: String,
                        override var created_at: TimeStamp,
                        override var updated_at: TimeStamp
                        ) extends Entity[Int] {
  override def save = inTransaction {
    super.save.asInstanceOf[Nationality]
  }

  def update = inTransaction {
    Nationality.findById(this.id).map { nationality =>
      val tmp = this.copy(created_at = nationality.created_at, updated_at = nationality.updated_at)
      tmp.save
    }
  }
}

object Nationality {

  import Database.{nationalityTable}

  implicit val nationalityWrites: Writes[Nationality] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(Nationality.unapply))

  implicit val nationalityReads: Reads[Nationality] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(Nationality.apply _)

  def allQ: Query[Nationality] = from(nationalityTable) {
    nationality => select(nationality)
  }

  def findAll: Iterable[Nationality] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(nationalityTable) {
      nationality => where(nationality.id === id) select (nationality)
    }.headOption
  }
}


case class RelationshipStatus(
                               id: Int,
                               name: String,
                               override var created_at: TimeStamp,
                               override var updated_at: TimeStamp
                               ) extends Entity[Int] {
  override def save = inTransaction {
    super.save.asInstanceOf[RelationshipStatus]
  }

  def update = inTransaction {
    RelationshipStatus.findById(this.id).map { nationality =>
      val tmp = this.copy(created_at = nationality.created_at, updated_at = nationality.updated_at)
      tmp.save
    }
  }
}

object RelationshipStatus {

  import Database.{relationshipStatusTable}

  implicit val nationalityWrites: Writes[RelationshipStatus] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(RelationshipStatus.unapply))

  implicit val nationalityReads: Reads[RelationshipStatus] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(RelationshipStatus.apply _)

  def allQ: Query[RelationshipStatus] = from(relationshipStatusTable) {
    nationality => select(nationality)
  }

  def findAll: Iterable[RelationshipStatus] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(relationshipStatusTable) {
      nationality => where(nationality.id === id) select (nationality)
    }.headOption
  }
}
