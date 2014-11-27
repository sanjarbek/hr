package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.Employee
import play.api.data.validation._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.templates.Html

object Employees extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.employees.list())
  }

  def jsonGet(id: Long) = Action {
    Employee.findById(id).map { employee =>
      Ok(Json.toJson(employee))
    }.getOrElse(NotFound)

    //    val employee = Employee.findById(id)
    //    Ok(Json.toJson(employee))
  }

  def jsonList = Action { request =>
    val employees = Employee.findAll.map { employee => Json.toJson(employee)}
    Ok(Json.toJson(employees))
  }

  def newEmployee = Action { implicit request =>
    Ok(views.html.employees.create())
  }

  def save = Action(parse.json) { implicit request =>
    val employeeJson = request.body
    employeeJson.validate[Employee].fold(
      valid = { employee =>
        val ret = Employee.insert(employee)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def show = Action {
    Ok(views.html.employees.show())
  }

  def edit = Action {
    Ok(views.html.employees.edit())
  }

  def update = Action(parse.json) { implicit request =>
    val employeeJson = request.body
    employeeJson.validate[Employee].fold(
      valid = { employee =>
        Employee.update(employee)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def template = Action {
    Ok(views.html.employees.default())
  }
}