package controllers

import play.api.mvc._
import models.{ContactInformation}
import play.api.libs.json._

object ContactInformations extends Controller with Security {

  def jsonEmployeeContact(employeeId: Long) = HasToken() { _ => currentId => implicit request =>
    ContactInformation.findEmployeeInformation(employeeId).map { contactInformation =>
      Ok(Json.toJson(contactInformation))
    }.getOrElse(Ok(JsNull))
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val structureJson = request.body
    structureJson.validate[ContactInformation].fold(
      valid = { contactInformation =>
        Ok(Json.toJson(contactInformation.save))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
    val structureJson = request.body
    structureJson.validate[ContactInformation].fold(
      valid = { structure =>
        structure.update
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    ContactInformation.findById(id).map { contactInformation =>
      val result = ContactInformation.delete(id)
      Ok(Json.toJson(result))
    }.getOrElse(NotFound(Json.toJson("Не найдена такая запись.")))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.contact_information.show())
  }

}