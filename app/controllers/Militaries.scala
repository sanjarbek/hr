package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Military, Employee}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Militaries extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.military.list())
  }

  def show = Action {
    Ok(views.html.military.show())
  }

  def jsonList = Action {
    val militaries = Military.findAll.map { military => Json.toJson(military)}
    Ok(Json.toJson(militaries))
  }

  def jsonEmployeeMilitary(employeeId: Long) = Action {
    Military.findEmployeeMilitary(employeeId).map {
      military => Ok(Json.toJson(military))
    }.getOrElse(Ok(JsNull))
  }

  def save = Action(parse.json) { implicit request =>
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

  def update = Action(parse.json) { implicit request =>
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

  def delete(id: Long) = Action { implicit request =>
    Military.delete(id)
    Ok(Json.toJson("Removed"))
  }

}