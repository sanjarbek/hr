package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Passport, Employee}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Passports extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.passport.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val passports = Passport.findAll.map { passport => Json.toJson(passport)}
    Ok(Json.toJson(passports))
  }

  def jsonEmployeePassport(employee_id: Long) = HasToken() { _ => currentId => implicit request =>
    Passport.findEmployeePassport(employee_id).map {
      passport => Ok(Json.toJson(passport))
    }.getOrElse(Ok(JsNull))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.passport.show())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val passportJson = request.body
    passportJson.validate[Passport].fold(
      valid = { passport =>
        val newPassport = Passport.insert(passport)
        Ok(Json.toJson(newPassport))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
    val passportJson = request.body
    passportJson.validate[Passport].fold(
      valid = { passport =>
        Passport.update(passport)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Passport.delete(id)
    Ok(Json.toJson("Removed"))
  }

}