package controllers

import models.{Nationality, RelationshipStatus}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object Entities extends Controller with Security {

  def jsonRelationshipStatusList = HasToken() { _ => currentId => implicit request =>
    val relationshipStatuses = RelationshipStatus.findAll.map { relationshipStatus => Json.toJson(relationshipStatus)}
    Ok(Json.toJson(relationshipStatuses))
  }

  def saveRelationshipStatus = HasToken(parse.json) { _ => currentId => implicit request =>
    val relationshipStatusJson = request.body
    relationshipStatusJson.validate[RelationshipStatus].fold(
      valid = { relationshipStatus =>
        val tmp: RelationshipStatus = relationshipStatus.save
        Ok(Json.toJson(tmp))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def jsonNationalityList = HasToken() { _ => currentId => implicit request =>
    val nationalities = Nationality.findAll.map { nationality => Json.toJson(nationality)}
    Ok(Json.toJson(nationalities))
  }

  def saveNationality = HasToken(parse.json) { _ => currentId => implicit request =>
    val nationalityJson = request.body
    nationalityJson.validate[Nationality].fold(
      valid = { nationality =>
        val tmp: Nationality = nationality.save
        Ok(Json.toJson(tmp))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

}
