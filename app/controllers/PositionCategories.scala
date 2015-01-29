package controllers

import play.api.mvc._
import models.{PositionType}
import play.api.libs.json._

object PositionCategories extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.position_category.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val position_categories = PositionType.findAll.map { position_categories => Json.toJson(position_categories)}
    Ok(Json.toJson(position_categories))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.position_category.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val positionCategoryJson = request.body
    positionCategoryJson.validate[PositionType].fold(
      valid = { positionCategory =>
        val pc = PositionType.insert(positionCategory)
        Ok(Json.toJson(pc))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    PositionType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.position_category.show())
  }

}
