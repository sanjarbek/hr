package controllers

import play.api.mvc._
import models.{Contract}
import play.api.libs.json._

object Contracts extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.contract.list())
  }

  def jsonList = Action {
    val contracts = Contract.findAll.map { contract => Json.toJson(contract)}
    Ok(Json.toJson(contracts))
  }

  def create = Action { implicit request =>
    Ok(views.html.contract.create())
  }

  def save = Action(parse.json) { implicit request =>
    val contractJson = request.body
    contractJson.validate[Contract].fold(
      valid = { contract =>
        val ret = Contract.insert(contract)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Contract.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.contract.show())
  }

}