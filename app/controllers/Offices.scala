package controllers

import play.api.mvc._
import models.{Office}
import play.api.libs.json._

object Offices extends Controller {

  def list = Action { implicit  request =>
    Ok(views.html.office.list())
  }

  def jsonList = Action {
    val office = Office.findAll.map { office => Json.toJson(office)}
    Ok(Json.toJson(office))
  }

  def create = Action { implicit request =>
    Ok(views.html.office.create())
  }

  def save = Action(parse.json) { implicit request =>
    val officeJson = request.body
    officeJson.validate[Office].fold(
      valid = { office =>
        Office.insert(office)
        Ok("Saved")
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def show = Action {
    Ok(views.html.office.show())
  }

}