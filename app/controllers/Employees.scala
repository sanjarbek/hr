package controllers

import java.time.LocalDateTime
import java.util.Date

import org.joda.time.DateTime
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{EmpTest, Employee}
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
        val tmp: Employee = employee.save
        Ok(Json.toJson(tmp))
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
        employee.update
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

  def jsonEmployeesList(status: Option[Boolean]) = Action {
    val employees = (status match {
      case Some(true) => Employee.findAllEmployed
      case Some(false) => Employee.findAllUnemployed
      case None => Employee.findAll
    }).map(employee => Json.toJson(employee))
    Ok(Json.toJson(employees))
  }

  def position(id: Long) = Action {
    Employee.getPosition(id).map { order =>
      Ok(Json.toJson(order))
    }.getOrElse(NotFound(Json.toJson("NotFound")))
  }

  def empTestList = Action {
    EmpTest.insert(EmpTest(0L, "Bakyt", java.time.LocalDate.of(1921, 12, 1), None, LocalDateTime.now(), None))
    val employees = EmpTest.findAll.map { employee => Json.toJson(employee)}
    Ok(Json.toJson(employees))
  }
}