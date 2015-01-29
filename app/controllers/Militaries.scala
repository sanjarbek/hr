package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Military, Employee}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Militaries extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.military.list())
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.military.show())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val militaries = Military.findAll.map { military => Json.toJson(military)}
    Ok(Json.toJson(militaries))
  }

  def jsonEmployeeMilitary(employeeId: Long) = HasToken() { _ => currentId => implicit request =>
    Military.findEmployeeMilitary(employeeId).map {
      military => Ok(Json.toJson(military))
    }.getOrElse(Ok(JsNull))
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val militaryJson = request.body
    militaryJson.validate[Military].fold(
      valid = { military =>
        val newMilitary = Military.insert(military)
        Ok(Json.toJson(newMilitary))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
    val militaryJson = request.body
    militaryJson.validate[Military].fold(
      valid = { military =>
        Military.update(military)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Military.delete(id)
    Ok(Json.toJson("Removed"))
  }

}