package controllers

import models.Seminar
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object Seminars extends Controller {

  def listTemplate = Action {
    Ok(views.html.seminar.list())
  }

  def jsonList(employeeId: Long) = Action {
    val seminars = Seminar.findEmployeeSeminars(employeeId).map { seminar => Json.toJson(seminar)}
    Ok(Json.toJson(seminars))
  }

  def save = Action(parse.json) { implicit request =>
    val seminarJson = request.body
    seminarJson.validate[Seminar].fold(
      valid = { seminar =>
        Ok(Json.toJson(seminar.save))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = Action(parse.json) { implicit request =>
    val seminarJson = request.body
    seminarJson.validate[Seminar].fold(
      valid = { seminar =>
        seminar.update
        Ok(Json.toJson("Обнавлен."))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action {
    Seminar.findById(id).map { seminar =>
      Seminar.delete(id)
      Ok(Json.toJson("Успешно удален."))
    }.getOrElse(NotFound(Json.toJson("Не найден.")))
  }

}
