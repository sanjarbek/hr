package controllers

import play.api.mvc._
import models.{RelationshipType}
import play.api.libs.json._

object RelationshipTypes extends Controller {

  def list = Action { implicit  request =>
    Ok(views.html.relationship_type.list())
  }

  def jsonList = Action {
    val relationship_types = RelationshipType.findAll.map { relationship_type => Json.toJson(relationship_type)}
    Ok(Json.toJson(relationship_types))
  }

  def create = Action { implicit request =>
    Ok(views.html.relationship_type.create())
  }

  def save = Action(parse.json) { implicit request =>
    val relationshipTypeJson = request.body
    relationshipTypeJson.validate[RelationshipType].fold(
      valid = { relationship_type =>
        val ret = RelationshipType.insert(relationship_type)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def show = Action {
    Ok(views.html.relationship_type.show())
  }

}