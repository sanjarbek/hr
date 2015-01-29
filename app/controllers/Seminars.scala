package controllers

import models.Seminar
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object Seminars extends Controller with Security {

  def listTemplate = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.seminar.list())
  }

  def jsonList(employeeId: Long) = HasToken() { _ => currentId => implicit request =>
    val seminars = Seminar.findEmployeeSeminars(employeeId).map { seminar => Json.toJson(seminar)}
    Ok(Json.toJson(seminars))
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def update = HasToken(parse.json) { _ => currentId => implicit request =>
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

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Seminar.findById(id).map { seminar =>
      Seminar.delete(id)
      Ok(Json.toJson("Успешно удален."))
    }.getOrElse(NotFound(Json.toJson("Не найден.")))
  }

}
