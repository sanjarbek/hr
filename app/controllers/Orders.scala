package controllers

import java.util.Date

import play.api._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import models.{Order}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import io.github.cloudify.scala.spdf._
import java.io._
import java.net._
import play.api.templates.Html

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Orders extends Controller {

  def create = Action { implicit request =>
    Ok(views.html.order.create())
  }

  def list = Action { implicit request =>
    Ok(views.html.order.list())
  }

  def jsonList = Action {
    val orders = Order.findAll.map { order => Json.toJson(order)}
    Ok(Json.toJson(orders))
  }

  def jsonGet(id: Long) = Action {
    Order.findById(id).map { order =>
      Ok(Json.toJson(order))
    }.getOrElse(NotFound)
  }

  def show = Action {
    Ok(views.html.order.show())
  }

  def save = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[Order].fold(
      valid = { order =>
        val newOrder = Order.insert(order)
        Ok(Json.toJson(newOrder))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def update = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[Order].fold(
      valid = { order =>
        Order.update(order)
        Ok(Json.toJson("Updated"))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    Order.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def generate(id: Long) = Action { implicit request =>
    val pdf = Pdf(Play.current.configuration.getString("wkhtmltopdf.executablePath").get, new PdfConfig {
      orientation := Portrait
      pageSize := Play.current.configuration.getString("wkhtmltopdf.pageSize").map { pageSize => pageSize}.getOrElse("A4")
      minimumFontSize := Play.current.configuration.getString("wkhtmltopdf.minimumFontSize").map { minimumFontSize => minimumFontSize.toInt}.getOrElse(16)
      marginTop := Play.current.configuration.getString("wkhtmltopdf.marginTop").map { marginTop => marginTop}.getOrElse("1cm")
      marginBottom := Play.current.configuration.getString("wkhtmltopdf.marginBottom").map { marginBottom => marginBottom}.getOrElse("1cm")
      marginLeft := Play.current.configuration.getString("wkhtmltopdf.marginLeft").map { marginLeft => marginLeft}.getOrElse("1cm")
      marginRight := Play.current.configuration.getString("wkhtmltopdf.marginRight").map { marginRight => marginRight}.getOrElse("1cm")
    })
    Order.findById(id).map { order =>
      val file = new File("order_template.pdf")
      val content = views.html.order.template.render(order, request)

      //    pdf.run(new URL("http://localhost:9001/#/employees/5"), file)

      pdf.run(content.toString(), file) match {
        case 0 => Ok.sendFile(content = file, fileName = _ => "termsOfService.pdf")
        case _ => Ok("Произошла ошибка при формировании pdf файла.")
      }
    }.getOrElse(NotFound("Не найден указанный приказ."))

  }

}