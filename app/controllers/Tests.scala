package controllers

import org.joda.time.DateTime
import play.api.mvc._
import models.{Test}
import play.api.libs.json._

object Tests extends Controller {
  def add = Action {
    val tmp = Test.insert(Test("Sanzhar"))
    Ok(tmp.name)
  }

  def update = Action {
    Test.findById(1092).map { tmp =>
      Test.insert(tmp)
      Ok("OK")
    }.getOrElse(NotFound("Не найден."))
  }
}