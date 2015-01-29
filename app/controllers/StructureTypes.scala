package controllers

import play.api.mvc._
import models.{StructureType}
import play.api.libs.json._

object StructureTypes extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.structure_type.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val structure_types = StructureType.findAll.map { structureType => Json.toJson(structureType)}
    Ok(Json.toJson(structure_types))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.structure_type.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val structureTypeJson = request.body
    structureTypeJson.validate[StructureType].fold(
      valid = { structureType =>
        Ok(Json.toJson(StructureType.insert(structureType)))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    StructureType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.structure_type.show())
  }

}