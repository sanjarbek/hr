package controllers

import play.api.mvc._
import models.{Institution}
import play.api.libs.json._

object Institutions extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.institution.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val institutions = Institution.findAll.map { institution => Json.toJson(institution)}
    Ok(Json.toJson(institutions))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.institution.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Institution.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.institution.show())
  }

}