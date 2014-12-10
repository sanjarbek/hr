package controllers

import models.{Nationality, RelationshipStatus}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object Entities extends Controller {

  def jsonRelationshipStatusList = Action {
    val relationshipStatuses = RelationshipStatus.findAll.map { relationshipStatus => Json.toJson(relationshipStatus)}
    Ok(Json.toJson(relationshipStatuses))
  }

  def saveRelationshipStatus = Action(parse.json) { implicit request =>
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

  def jsonNationalityList = Action {
    val nationalities = Nationality.findAll.map { nationality => Json.toJson(nationality)}
    Ok(Json.toJson(nationalities))
  }

  def saveNationality = Action(parse.json) { implicit request =>
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
