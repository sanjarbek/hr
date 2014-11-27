package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import models.{QualificationType, Education}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Educations extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.education.list())
  }

  def jsonList = Action {
    val educations = Education.findAll.map { education => Json.toJson(education)}
    Ok(Json.toJson(educations))
  }

  def jsonEmployeeFamily(employeeId: Long) = Action {
    val educations = Education.findEmployeeEducations(employeeId).map { education => Json.toJson(education)}
    Ok(Json.toJson(educations))
  }

  def create = Action { implicit request =>
    Ok(views.html.education.create())
  }

  def save = Action(parse.json) { implicit request =>
    val educationJson = request.body
    educationJson.validate[Education].fold(
      valid = { education =>
        val ret = Education.insert(education)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = Action(parse.json) { implicit request =>
    val educationJson = request.body
    educationJson.validate[Education].fold(
      valid = { education =>
        Education.update(education)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Education.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.education.show())
  }

  def jsonQualificationTypesList() = Action {
    val qualificationTypes = QualificationType.findAll.map { qualification => Json.toJson(qualification)}
    Ok(Json.toJson(qualificationTypes))
  }

  def qualificationTypeSave() = Action(parse.json) { implicit request =>
    val qualificationTypeJson = request.body
    qualificationTypeJson.validate[QualificationType].fold(
      valid = { qualificationType =>
        Ok(Json.toJson(QualificationType.insert(qualificationType)))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }
}