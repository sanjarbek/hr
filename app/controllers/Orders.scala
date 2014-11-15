package controllers

import java.util.Date

import play.api._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import models.{OrderTag, Order}
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
    //    Ok(Json.toJson(orders))
    Unauthorized(Json.obj("auth" -> "false"))
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
        newOrder.tags.map(tags =>
          tags.split(',').foreach(tag =>
            OrderTag.findById(tag).map { order_tag =>
              OrderTag.update(OrderTag(order_tag.id, order_tag.count + 1))
            }.getOrElse {
              OrderTag.insert(OrderTag(tag, 1))
            }
          )
        )
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
        Order.findById(order.id).map { order_old =>
          Order.update(order)
          val tagsListOld = order_old.tags.map { t1 => t1.split(",").toList}.getOrElse(List())
          val tagsListNew = order.tags.map { t1 => t1.split(",").toList}.getOrElse(List())
          val diffOld = tagsListOld.diff(tagsListNew)
          val diffNew = tagsListNew.diff(tagsListOld)

          diffOld.foreach(tag =>
            OrderTag.findById(tag).map { order_tag =>
              OrderTag.update(OrderTag(order_tag.id, order_tag.count - 1))
            }
          )
          diffNew.foreach(tag =>
            OrderTag.findById(tag).map { order_tag =>
              OrderTag.update(OrderTag(order_tag.id, order_tag.count + 1))
            }.getOrElse {
              OrderTag.insert(OrderTag(tag, 1))
            }
          )

          Ok(Json.toJson("Updated"))
        }.getOrElse(NotFound("Не найдена такая запись."))
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

  def tagsList(query: String) = Action {
    val tags = OrderTag.findByName(query)
    Ok(Json.toJson(tags))
  }

}