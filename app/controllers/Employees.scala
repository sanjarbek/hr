package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.Employee
import play.api.data.validation._

object Employees extends Controller {

  private val employeeForm: Form[Employee] = Form(
    mapping(
      "id" -> ignored(0L),
      "surname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "firstname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "lastname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "birthday" -> date,
      "citizenship" -> nonEmptyText(minLength = 2, maxLength = 30),
      "insurance_number" -> nonEmptyText(minLength = 2, maxLength = 20),
      "tax_number" -> nonEmptyText(minLength = 2, maxLength = 20),
      "home_phone" -> nonEmptyText(minLength = 2, maxLength = 20),
      "mobile_phone" -> nonEmptyText(minLength = 2, maxLength = 20),
      "email" -> nonEmptyText(minLength = 2, maxLength = 20)
    )(Employee.apply)(Employee.unapply)
  )

  def index = Action { implicit  request =>
    val employees = Employee.findAll
    Ok(views.html.employees.list(employees))
  }

  def newEmployee = Action { implicit request =>
    Ok(views.html.employees.create(employeeForm))
  }

  def save = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      hasErrors = { form =>
        Ok(views.html.employees.create(form))
      },
      success = { newEmployee =>
        val employee = Employee.insert(newEmployee)
        Redirect(routes.Employees.show(employee.id))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Employee.findById(id).map{ employee =>
        Ok(views.html.employees.show(employee))
    }.getOrElse(NotFound)
  }
}