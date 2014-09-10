package controllers

import play.api.mvc._
import models.{ContractType}
import play.api.libs.json._

object ContractTypes extends Controller {

  def list = Action { implicit request =>
    Ok(views.html.contract_type.list())
  }

  def jsonList = Action {
    val contract_types = ContractType.findAll.map { contract_type => Json.toJson(contract_type)}
    Ok(Json.toJson(contract_types))
  }

  def create = Action { implicit request =>
    Ok(views.html.contract_type.create())
  }

  def save = Action(parse.json) { implicit request =>
    val contractTypeJson = request.body
    contractTypeJson.validate[ContractType].fold(
      valid = { contract_type =>
        val ret = ContractType.insert(contract_type)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    ContractType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = Action {
    Ok(views.html.contract_type.show())
  }

}