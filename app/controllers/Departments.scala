package controllers

import play.api.mvc._
import models.{Department}
import play.api.libs.json._

object Departments extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.departments.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val departments = Department.findAll.map { department => Json.toJson(department)}
    Ok(Json.toJson(departments))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.departments.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Department.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.departments.show())
  }

}