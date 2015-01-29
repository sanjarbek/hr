package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import models.{QualificationType, Education}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Educations extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.education.list())
  }

  def jsonList(employeeId: Long) = HasToken() { _ => currentId => implicit request =>
    val educations = Education.findEmployeeEducations(employeeId).map { education => Json.toJson(education)}
    Ok(Json.toJson(educations))
  }

  def jsonEmployeeFamily(employeeId: Long) = HasToken() { _ => currentId => implicit request =>
    val educations = Education.findEmployeeEducations(employeeId).map { education => Json.toJson(education)}
    Ok(Json.toJson(educations))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.education.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Education.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.education.show())
  }

  def jsonQualificationTypesList() = HasToken() { _ => currentId => implicit request =>
    val qualificationTypes = QualificationType.findAll.map { qualification => Json.toJson(qualification)}
    Ok(Json.toJson(qualificationTypes))
  }

  def qualificationTypeSave() = HasToken(parse.json) { _ => currentId => implicit request =>
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