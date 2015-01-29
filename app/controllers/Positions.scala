package controllers

import play.api.mvc._
import models.{Position}
import play.api.libs.json._

object Positions extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.position.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val positions = Position.findAll.map { position => Json.toJson(position)}
    Ok(Json.toJson(positions))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.position.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val positionJson = request.body
    positionJson.validate[Position].fold(
      valid = { position =>
        Ok("Saved")
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.position.show())
  }

}