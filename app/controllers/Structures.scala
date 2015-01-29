package controllers

import play.api.Logger
import play.api.mvc._
import models.{Structure}
import play.api.libs.json._

object Structures extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.structures.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val structures = Structure.findAll.map { structure =>
      Json.toJson(structure)
    }
    Ok(Json.toJson(structures))
  }

  def jsonFreePositionsList = HasToken() { _ => currentId => implicit request =>
    val structures = Structure.findFreePositions.map { structure => Json.toJson(structure)}
    Ok(Json.toJson(structures))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.structures.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Structure.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.structures.show())
  }

  def jsonGet(structureId: Long) = HasToken() { _ => currentId => implicit request =>
    Structure.findById(structureId).map { structure =>
      Ok(Json.toJson(structure))
    }.getOrElse(NotFound(Json.toJson("Не найден")))
  }
}