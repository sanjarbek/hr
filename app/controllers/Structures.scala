package controllers

import play.api.Logger
import play.api.mvc._
import models.{Structure}
import play.api.libs.json._

object Structures extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.structures.list())
  }

  def jsonList = Action {
    val structures = Structure.findAll.map { structure =>
      Json.toJson(structure)
    }
    Ok(Json.toJson(structures))
  }

  def jsonFreePositionsList = Action {
    val structures = Structure.findFreePositions.map { structure => Json.toJson(structure)}
    Ok(Json.toJson(structures))
  }

  def create = Action { implicit request =>
    Ok(views.html.structures.create())
  }

  def save = Action(parse.json) { implicit request =>
    val structureJson = request.body
    structureJson.validate[Structure].fold(
      valid = { structure =>
        Ok(Json.toJson(Structure.insert(structure)))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = Action(parse.json) { implicit request =>
    val structureJson = request.body
    structureJson.validate[Structure].fold(
      valid = { structure =>
        Structure.update(structure)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Structure.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.structures.show())
  }

}