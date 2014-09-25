package controllers

import play.api.mvc._
import models.{PositionType}
import play.api.libs.json._

object PositionCategories extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.position_category.list())
  }

  def jsonList = Action {
    val position_categories = PositionType.findAll.map { position_categories => Json.toJson(position_categories)}
    Ok(Json.toJson(position_categories))
  }

  def create = Action { implicit request =>
    Ok(views.html.position_category.create())
  }

  def save = Action(parse.json) { implicit request =>
    val positionCategoryJson = request.body
    positionCategoryJson.validate[PositionType].fold(
      valid = { positionCategory =>
        PositionType.insert(positionCategory)
        Ok("Saved")
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    PositionType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.position_category.show())
  }

}
