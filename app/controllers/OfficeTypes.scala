package controllers

import play.api.mvc._
import models.{OfficeType}
import play.api.libs.json._

object OfficeTypes extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.office_type.list())
  }

  def jsonList = Action {
    val office_types = OfficeType.findAll.map { office_type => Json.toJson(office_type)}
    Ok(Json.toJson(office_types))
  }

  def create = Action { implicit request =>
    Ok(views.html.office_type.create())
  }

  def save = Action(parse.json) { implicit request =>
    val officeTypeJson = request.body
    officeTypeJson.validate[OfficeType].fold(
      valid = { office_type =>
        OfficeType.insert(office_type)
        Ok("Saved")
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    OfficeType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.office_type.show())
  }

}