package controllers

import play.api.mvc._
import models.{Institution}
import play.api.libs.json._

object Institutions extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.institution.list())
  }

  def jsonList = Action {
    val institutions = Institution.findAll.map { institution => Json.toJson(institution)}
    Ok(Json.toJson(institutions))
  }

  def create = Action { implicit request =>
    Ok(views.html.institution.create())
  }

  def save = Action(parse.json) { implicit request =>
    val institutionJson = request.body
    institutionJson.validate[Institution].fold(
      valid = { institution =>
        val ret = Institution.insert(institution)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Institution.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.institution.show())
  }

}