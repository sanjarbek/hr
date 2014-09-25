package controllers

import play.api.mvc._
import models.{StructureType}
import play.api.libs.json._

object StructureTypes extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.structure_type.list())
  }

  def jsonList = Action {
    val structure_types = StructureType.findAll.map { structureType => Json.toJson(structureType)}
    Ok(Json.toJson(structure_types))
  }

  def create = Action { implicit request =>
    Ok(views.html.structure_type.create())
  }

  def save = Action(parse.json) { implicit request =>
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

  def delete(id: Long) = Action { implicit request =>
    StructureType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.structure_type.show())
  }

}