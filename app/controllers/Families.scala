package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.Family

object Families extends Controller {

  private val familyForm: Form[Family] = Form(
    mapping(
      "id" -> ignored(0L),
      "employee_id" -> longNumber,
      "relationship" -> number,
      "surname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "firstname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "lastname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "birthday" -> date
    )(Family.apply)(Family.unapply)
  )

  def index = Action { implicit request =>
    val family = Family.findAll
    Ok(views.html.family.list(family))
  }

  def newFamily(employeeId: Long) = Action { implicit request =>
    Ok(views.html.family.create(familyForm, employeeId))
  }

  def save(employeeId: Long) = Action { implicit request =>
    familyForm.bindFromRequest().fold(
      hasErrors = { form =>
        Ok(views.html.family.create(form, employeeId))
      },
      success = { family =>
        val nfamily = Family.insert(family)
        Redirect(routes.Families.show(nfamily.id))
      }
    )
  }

  def show(id: Long) = Action { implicit request =>
    Family.findById(id).map{ family =>
        Ok(views.html.family.show(family))
    }.getOrElse(NotFound)
  }

  def family(employeeId: Long) = Action { implicit request =>
    val employeeFamilyList = Family.findEmployeeFamily(employeeId)
    Ok(views.html.family.list(employeeFamilyList))
  }

}