package controllers

import java.text.SimpleDateFormat
import java.util.Calendar

import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.mvc.{WebSocket, Controller}

object Application extends Controller {

  def serverStatus() = WebSocket.using[String] { implicit request =>
    val in = Iteratee.ignore[String]
    val out = Enumerator.repeatM {
      Promise.timeout(this.getConnectionStatus, 1)
    }
    (in, out)
  }

  def getConnectionStatus = Calendar.getInstance().getTime.toLocaleString()

  def admin = Action{
    Ok(views.html.admin())
  }

}