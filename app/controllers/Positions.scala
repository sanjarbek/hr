package controllers

import play.api.mvc._
import models.{Position}
import play.api.libs.json._

object Positions extends Controller {

  def list = Action { implicit  request =>
    Ok(views.html.position.list())
  }

  def jsonList = Action {
    val positions = Position.findAll.map { position => Json.toJson(position)}
    Ok(Json.toJson(positions))
  }

  def create = Action { implicit request =>
    Ok(views.html.position.create())
  }

  def save = Action(parse.json) { implicit request =>
    val positionJson = request.body
    positionJson.validate[Position].fold(
      valid = { position =>
        Position.insert(position)
        Ok("Saved")
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def show = Action {
    Ok(views.html.position.show())
  }

}