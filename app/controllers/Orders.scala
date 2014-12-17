package controllers

import java.time.{LocalDateTime, LocalDate}
import java.util.Date

import org.joda.time.Instant
import play.api._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import io.github.cloudify.scala.spdf._
import java.io._
import java.net._
import play.api.templates.Html
import models.Database.MyLocalDate
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Orders extends Controller with Security {

  def create = Action { implicit request =>
    Ok(views.html.order.create())
  }

  def createEmployment = Action { implicit request =>
    Ok(views.html.order.employment.create())
  }

  def saveEmployment = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[EmploymentOrder].fold(
      valid = { order =>
        val newOrder = order.save
        Ok(Json.toJson(order))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def tabTemplate = Action {
    Ok(views.html.order.tabTemplate())
  }

  def list = Action { implicit request =>
    Ok(views.html.order.list())
  }

  def listEmployment = Action { implicit request =>
    Ok(views.html.order.employment.list())
  }

  def addEmploymentOrder = Action {
    import models.Database.localDateToMyLocalDate
    val temp = EmploymentOrder(0, 1, 1, Instant.now().toDate, 1, 1, 1, 1, 30000, 1, Some(Instant.now().toDate), Some(Instant.now.toDate), Instant.now.toDate, None, None, LocalDateTime.now, LocalDateTime.now).save
    val tmp = DismissalOrder(0, 1, 3, Instant.now().toDate, 1, 1, "Test", Instant.now().toDate, null, null).save
    Ok("Saved")
    //    Ok(Json.toJson(temp))
    //    EmploymentOrder.findById(1).map{ employmentOrder =>
    //      Ok(Json.toJson(employmentOrder))
    //    }.getOrElse(NotFound(Json.toJson("Не найден")))
  }

  //  def jsonList() = HasToken() { _ => currentId => implicit request =>
  //    val orders = Order.findAll.map { order => Json.toJson(order)}
  //    Ok(Json.toJson(orders))
  //  }

  def jsonList() = Action { implicit request =>
    val orders = Order.findAll.map { order => Json.toJson(order)}
    Ok(Json.toJson(orders))
  }

  def jsonEmploymentList() = Action { implicit request =>
    case class EmploymentOrderFull(order: EmploymentOrder, employee: Employee, calendarType: CalendarType, contractType: ContractType)
    implicit val testWrites: Writes[EmploymentOrderFull] = (
      (JsPath \ "order").write[EmploymentOrder] and
        (JsPath \ "employee").write[Employee] and
        (JsPath \ "calendarType").write[CalendarType] and
        (JsPath \ "contractType").write[ContractType]
      )(unlift(EmploymentOrderFull.unapply))

    implicit val testReads: Reads[EmploymentOrderFull] = (
      (JsPath \ "order").read[EmploymentOrder] and
        (JsPath \ "employee").read[Employee] and
        (JsPath \ "calendarType").read[CalendarType] and
        (JsPath \ "contractType").read[ContractType]
      )(EmploymentOrderFull.apply _)

    val employees = EmploymentOrder.findFull.map { order => Json.toJson(EmploymentOrderFull(order._1, order._2, order._3, order._4))}
    Ok(Json.toJson(employees))
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
        //        val newOrder = Order.insert(order)
        Ok(Json.toJson(order))
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
          //          Order.update(order)
          Ok(Json.toJson("Updated"))
        }.getOrElse(NotFound("Не найдена такая запись."))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = Action { implicit request =>
    //    Order.delete(id)
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

object Orders extends Orders {

}