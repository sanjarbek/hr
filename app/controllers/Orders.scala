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

  def listEmployment = Action { implicit request =>
    Ok(views.html.order.employment.list())
  }

  def jsonEmploymentList() = Action { implicit request =>
    case class EmploymentOrderFull(order: EmploymentOrder, employee: Employee, calendarType: CalendarType, contractType: ContractType, position: Structure)
    implicit val testWrites: Writes[EmploymentOrderFull] = (
      (JsPath \ "order").write[EmploymentOrder] and
        (JsPath \ "employee").write[Employee] and
        (JsPath \ "calendarType").write[CalendarType] and
        (JsPath \ "contractType").write[ContractType] and
        (JsPath \ "position").write[Structure]
      )(unlift(EmploymentOrderFull.unapply))

    implicit val testReads: Reads[EmploymentOrderFull] = (
      (JsPath \ "order").read[EmploymentOrder] and
        (JsPath \ "employee").read[Employee] and
        (JsPath \ "calendarType").read[CalendarType] and
        (JsPath \ "contractType").read[ContractType] and
        (JsPath \ "position").read[Structure]
      )(EmploymentOrderFull.apply _)

    val employees = EmploymentOrder.findFull.map { order => Json.toJson(EmploymentOrderFull(order._1, order._2, order._3, order._4, order._5))}
    Ok(Json.toJson(employees))
  }

  def jsonEmploymentOrderGet(employmentOrderId: Long) = Action {
    EmploymentOrder.findById(employmentOrderId).map { employmentOrder =>
      Ok(Json.toJson(employmentOrder))
    }.getOrElse(NotFound(Json.toJson("Not found")))
  }

  def saveEmployment = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[EmploymentOrder].fold(
      valid = { order =>
        val newOrder = order.save
        val position = Position(0, newOrder.id, newOrder.position_id, newOrder.employee_id, None,
          newOrder.start_date, newOrder.end_date, newOrder.close_date, null, null).save
        Ok(Json.toJson(newOrder))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def updateEmployment = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[EmploymentOrder].fold(
      valid = { order =>
        order.update.map { new_order =>
          Position.findByEmploymentOrderId(new_order.id).map { position =>
            position.copy(position_id = new_order.position_id,
              employee_id = new_order.employee_id, start_date = new_order.start_date,
              end_date = new_order.end_date, close_date = new_order.close_date
            ).update
          }
          Ok(Json.toJson(new_order))
        }.getOrElse(NotFound(Json.toJson("Не найден")))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def createDismissal = Action { implicit request =>
    Ok(views.html.order.dismissal.create())
  }

  def listDismissal = Action { implicit request =>
    Ok(views.html.order.dismissal.list())
  }


  def jsonDismissalList() = Action { implicit request =>

    case class DismissalOrderFull(order: DismissalOrder, employee: Employee, position: Structure)

    implicit val testWrites: Writes[DismissalOrderFull] = (
      (JsPath \ "order").write[DismissalOrder] and
        (JsPath \ "employee").write[Employee] and
        (JsPath \ "position").write[Structure]
      )(unlift(DismissalOrderFull.unapply))

    implicit val testReads: Reads[DismissalOrderFull] = (
      (JsPath \ "order").read[DismissalOrder] and
        (JsPath \ "employee").read[Employee] and
        (JsPath \ "position").read[Structure]
      )(DismissalOrderFull.apply _)

    val dismissals = DismissalOrder.findFull.map { order => Json.toJson(DismissalOrderFull(order._1, order._2, order._3))}
    Ok(Json.toJson(dismissals))
  }

  def jsonDismissalOrderGet(employmentOrderId: Long) = Action {
    EmploymentOrder.findById(employmentOrderId).map { employmentOrder =>
      Ok(Json.toJson(employmentOrder))
    }.getOrElse(NotFound(Json.toJson("Not found")))
  }

  def saveDismissal = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[DismissalOrder].fold(
      valid = { order =>
        val newOrder = order.save
        Position.findByEmployeeId(newOrder.employee_id).reduceLeftOption {
          (maxElement, element) => if (maxElement.start_date.after(element.start_date)) maxElement else element
        } match {
          case Some(position) => position.copy(end_date = Some(newOrder.leaving_date), dismissal_order_id = Some(newOrder.id)).update
          case None => Logger.info("Сотрудник и так не принят на работу.")
        }
        Ok(Json.toJson(newOrder))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def jsonLeavingReasonList = Action {
    val leavingReasons = LeavingReason.findAll.map(leavingReason => Json.toJson(leavingReason))
    Ok(Json.toJson(leavingReasons))
  }

  def saveLeavingReason = Action(parse.json) { implicit request =>
    val leavingReasonJson = request.body
    leavingReasonJson.validate[LeavingReason].fold(
      valid = { leavingReason =>
        val tmp = leavingReason.save
        Ok(Json.toJson(tmp))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def updateDismissal = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[DismissalOrder].fold(
      valid = { order =>
        order.update.map { new_order =>
          Position.findByDismissalOrderId(new_order.id).map { position =>
            position.copy(close_date = Some(new_order.leaving_date), employee_id = new_order.employee_id).update
          }
          Ok(Json.toJson(new_order))
        }.getOrElse(NotFound(Json.toJson("Не найден")))
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

  def addEmploymentOrder = Action {
    //    Position.findByEmployeeId(7).max
    Ok("Saved")
  }

  //  def jsonList() = HasToken() { _ => currentId => implicit request =>
  //    val orders = Order.findAll.map { order => Json.toJson(order)}
  //    Ok(Json.toJson(orders))
  //  }

  def jsonList() = Action { implicit request =>
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