package controllers

import java.util.Date

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Relationship,Employee}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Relationships extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.relationship.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val relationships = Relationship.findAll.map { relationship => Json.toJson(relationship)}
    Ok(Json.toJson(relationships))
  }

  def jsonEmployeeFamily(employeeId: Long) = HasToken() { _ => currentId => implicit request =>
    val relationships = Relationship.findEmployeeFamily(employeeId).map { relationship => Json.toJson(relationship)}
    Ok(Json.toJson(relationships))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.relationship.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val relationshipJson = request.body
    relationshipJson.validate[Relationship].fold(
      valid = { relationship =>
        val newRelationship = Relationship.insert(relationship)
        Ok(Json.toJson(newRelationship))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
    val relationshipJson = request.body
    relationshipJson.validate[Relationship].fold(
      valid = { relationship =>
        Relationship.update(relationship)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Relationship.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.relationship.show())
  }

}