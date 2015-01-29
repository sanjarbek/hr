package controllers

import play.api.mvc._
import models.{RelationshipType}
import play.api.libs.json._

object RelationshipTypes extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.relationship_type.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val relationship_types = RelationshipType.findAll.map { relationship_type => Json.toJson(relationship_type)}
    Ok(Json.toJson(relationship_types))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.relationship_type.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    RelationshipType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.relationship_type.show())
  }

}