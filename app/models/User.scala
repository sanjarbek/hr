package models

import java.time.{LocalDateTime}

import models.MyCustomTypes._
import org.mindrot.jbcrypt.BCrypt
import org.squeryl.Query
import org.squeryl.annotations._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}

import scala.collection.Iterable

case class User(
                 id: Int,
                 employee_id: Long,
                 username: String,
                 @Column("password_hash") passwordHash: String,
                 @Column("password_reset_token") passwordResetToken: Option[String],
                 @Column("last_activity") lastActivityTime: Option[LocalDateTime],
                 status: Int,
                 override var created_at: LocalDateTime,
                 override var updated_at: LocalDateTime
                 ) extends Entity[Int] {
  override def save = inTransaction {
    super.save.asInstanceOf[User]
  }

  def update = inTransaction {
    User.findById(this.id).map { user =>
      val tmp = this.copy(created_at = user.created_at, updated_at = user.updated_at)
      tmp.save
    }
  }

  def employee = inTransaction {
    from(Database.employeeTable) { employee =>
      where(employee.id === this.id) select (employee)
    }.headOption
  }
}


object User {

  import Database.{userTable, employeeTable}

  implicit val userWrites: Writes[User] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "employee_id").write[Long] and
      (JsPath \ "username").write[String] and
      (JsPath \ "passwordHash").write[String] and
      (JsPath \ "passwordResetToken").write[Option[String]] and
      (JsPath \ "lastActivityTime").write[Option[LocalDateTime]] and
      (JsPath \ "status").write[Int] and
      (JsPath \ "created_at").write[LocalDateTime] and
      (JsPath \ "updated_at").write[LocalDateTime]
    )(unlift(User.unapply))

  implicit val userReads: Reads[User] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "employee_id").read[Long] and
      (JsPath \ "username").read[String] and
      (JsPath \ "passwordHash").read[String] and
      (JsPath \ "passwordResetToken").read[Option[String]] and
      (JsPath \ "lastActivityTime").read[Option[LocalDateTime]] and
      (JsPath \ "status").read[Int] and
      (JsPath \ "created_at").read[LocalDateTime] and
      (JsPath \ "updated_at").read[LocalDateTime]
    )(User.apply _)

  def allQ: Query[User] = from(userTable) {
    certificate => select(certificate)
  }

  def findAll: Iterable[User] = inTransaction {
    allQ.toList
  }

  def findById(id: Int) = inTransaction {
    from(userTable) {
      user => where(user.id === id) select (user)
    }.headOption
  }

  def findByUsername(username: String) = inTransaction {
    from(userTable) { user =>
      where(user.username === username) select (user)
    }.headOption
  }

  def hashPassword(p: String) = BCrypt.hashpw(p, BCrypt.gensalt())

  def findAllWithEmployeeInfo = inTransaction {
    from(userTable, employeeTable) { (user, employee) =>
      where(user.employee_id === employee.id) select(user, employee)
    }.toList
  }

}
