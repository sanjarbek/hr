package controllers

import play.api.mvc._
import models.{Department}
import play.api.libs.json._

object Departments extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.departments.list())
  }

  def jsonList = Action {
    val departments = Department.findAll.map { department => Json.toJson(department)}
    Ok(Json.toJson(departments))
  }

  def create = Action { implicit request =>
    Ok(views.html.departments.create())
  }

  def save = Action(parse.json) { implicit request =>
    val departmentJson = request.body
    departmentJson.validate[Department].fold(
      valid = { department =>
        Ok(Json.toJson(Department.insert(department)))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def show = Action {
    Ok(views.html.departments.show())
  }

}