package controllers

import play.api.mvc._
import models.{ContactInformation}
import play.api.libs.json._

object ContactInformations extends Controller {

  def jsonEmployeeContact(employeeId: Long) = Action {
    ContactInformation.findEmployeeInformation(employeeId).map { contactInformation =>
      Ok(Json.toJson(contactInformation))
    }.getOrElse(Ok(JsNull))
  }

  def save = Action(parse.json) { implicit request =>
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

  def update = Action(parse.json) { implicit request =>
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

  def delete(id: Long) = Action { implicit request =>
    ContactInformation.findById(id).map { contactInformation =>
      val result = ContactInformation.delete(id)
      Ok(Json.toJson(result))
    }.getOrElse(NotFound(Json.toJson("Не найдена такая запись.")))
  }

  def show = Action {
    Ok(views.html.contact_information.show())
  }

}