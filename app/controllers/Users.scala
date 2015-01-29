package controllers

import java.time.LocalDateTime

import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object Users extends Controller with Security {

  //  def createTest = Action {
  //    val username = "admin"
  //    val password = "test12"
  //
  //    // Hash a password for the first time
  //    val hashed = BCrypt.hashpw(password, BCrypt.gensalt());
  //
  //    val user = User(0, username, hashed, None, None, 1, LocalDateTime.now, LocalDateTime.now)
  //
  //    val updated = user.save
  //
  //    Ok(Json.toJson(updated))
  //  }

  def checkPassword = HasToken() { _ => currentId => implicit request =>
    val username = "admin"
    val password = "test12"

    User.findById(1).map { user =>
      if (BCrypt.checkpw(password, user.passwordHash))
        Ok(Json.toJson("Совпадает"))
      else
        Ok(Json.toJson("Не совпадает"))
    }.getOrElse(NotFound(Json.toJson("Данный пользователь не найден")))

  }

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.user.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val users = User.findAll map { user => Json.toJson(user)}
    Ok(Json.toJson(users))
  }

  def changePassword = HasToken(parse.json) { _ => currentid => request =>
    case class UserChangePassword(id: Int, password: String)
    implicit val userChangeWrites: Reads[UserChangePassword] = (
      (JsPath \ "id").read[Int] and
        (JsPath \ "password").read[String]
      )(UserChangePassword.apply _)

    val newUserPassword = request.body
    newUserPassword.validate[UserChangePassword].fold(
      valid = { newPassword =>
        User.findById(newPassword.id).map { user =>
          user.copy(passwordHash = User.hashPassword(newPassword.password)).update
          Ok(Json.toJson("Success"))
        } getOrElse NotFound(Json.toJson("Not such user"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def create = HasToken(parse.json) { _ => currentid => request =>
    val userJson = request.body
    userJson.validate[User].fold(
      valid = { user =>
        val newUser = user.copy(passwordHash = User.hashPassword(user.passwordHash)).save
        Ok(Json.toJson(newUser))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = HasToken(parse.json) { _ => currentid => request =>
    val userJson = request.body
    userJson.validate[User].fold(
      valid = { user =>
        User.findById(user.id).map { tmp =>
          Ok(Json.toJson(tmp.copy(username = user.username).update))
        } getOrElse NotFound(Json.toJson("Не существует такого пользователя"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def changeStatus = HasToken(parse.json) { _ => currentid => request =>
    case class UserChangeStatus(id: Int, status: Int)
    implicit val userChangeWrites: Reads[UserChangeStatus] = (
      (JsPath \ "id").read[Int] and
        (JsPath \ "status").read[Int]
      )(UserChangeStatus.apply _)

    val newUserStatus = request.body
    newUserStatus.validate[UserChangeStatus].fold(
      valid = { newStatus =>
        User.findById(newStatus.id) map { user =>
          user.copy(status = newStatus.status).update
          Ok(Json.toJson("Success"))
        } getOrElse NotFound(Json.toJson("Не существует такого пользователя"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

}
