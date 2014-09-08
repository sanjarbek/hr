package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Relationship,Employee}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Relationships extends Controller {

  private val relationshipForm: Form[Relationship] = Form(
    mapping(
      "id" -> ignored(0L),
      "employee_id" -> longNumber.verifying("There is not exists employee.", Employee.findById(_).nonEmpty),
      "degree" -> number,
      "surname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "firstname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "lastname" -> nonEmptyText(minLength = 2, maxLength = 20),
      "birthday" -> date
    )(Relationship.apply)(Relationship.unapply)
  )

  def list = Action { implicit  request =>
    Ok(views.html.relationship.list())
  }

  def jsonList = Action {
    val relationships = Relationship.findAll.map { relationship => Json.toJson(relationship)}
    Ok(Json.toJson(relationships))
  }

  def jsonEmployeeFamily(employeeId: Long) = Action {
    val relationships = Relationship.findEmployeeFamily(employeeId).map { relationship => Json.toJson(relationship)}
    Ok(Json.toJson(relationships))
  }

  def create = Action { implicit request =>
    Ok(views.html.relationship.create())
  }

  def save = Action(parse.json) { implicit request =>
    val relationshipJson = request.body
    relationshipJson.validate[Relationship].fold(
      valid = { relationship =>
        val newRelationship = Relationship.insert(relationship)
        Ok(Json.toJson(newRelationship))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def show = Action {
    Ok(views.html.relationship.show())
  }

}