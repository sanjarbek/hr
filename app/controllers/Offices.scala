package controllers

import play.api.mvc._
import models.{Office}
import play.api.libs.json._

object Offices extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.office.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val office = Office.findAll.map { office => Json.toJson(office)}
    Ok(Json.toJson(office))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.office.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val officeJson = request.body
    officeJson.validate[Office].fold(
      valid = { office =>
        Office.insert(office)
        Ok("Saved")
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Office.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.office.show())
  }

}