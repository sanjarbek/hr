package controllers

import java.text.SimpleDateFormat
import java.time.temporal.{TemporalUnit, TemporalAmount}
import java.time.{ZoneOffset, Period, LocalDateTime, LocalDate}
import java.util.{UUID, Calendar, Date}

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
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
import models.MyCustomTypes._


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

  def getEmploymentOrderDocument(employmentOrderId: Long) = Action {

    EmploymentOrder.findById(employmentOrderId).map { employmentOrder =>
      ContractType.findById(employmentOrder.contract_type_id).map { contractType =>
        contractType.file_path match {
          case Some(filePath) => {
            Employee.findById(employmentOrder.employee_id).map { employee =>
              val fileName = "testx.doc"
              val filePath = s"./uploaded/documents/$fileName"
              val fs = new POIFSFileSystem(new FileInputStream(filePath))
              var docx = new HWPFDocument(fs)
              val dateFormatter = new SimpleDateFormat("dd.MM.yyyy")

              val position = Structure.findById(employmentOrder.position_id).get

              val department = Structure.findById(position.parent_id.get).get


              val current_date = LocalDate.now
              val trial_start = employmentOrder.trial_period_start match {
                case Some(date) => date
                case None => current_date
              }
              val trial_end = employmentOrder.trial_period_end match {
                case Some(date) => date
                case None => current_date
              }
              val trial_period = Period.between(trial_start, trial_end).getMonths

              val keywords = Map(
                "<Работник>" -> s"${employee.surname} ${employee.firstname} ${employee.lastname}",
                "<Подразделение>" -> department.name,
                "<Должность>" -> position.name,
                "<Условия приема на работу>" -> contractType.name,
                "<Испытательный cрок>" -> "",
                "<Трудовой договор дата>" -> (dateFormatter.format(employmentOrder.start_date).toString),
                "<Трудовой договор номер>" -> employmentOrder.contract_number.toString,
                "<ФИО Руководителя>" -> "Бапа уулу Кубанычбек",
                "<Табельный номер>" -> employee.id.toString,
                "<Испытательный cрок>" -> trial_period.toString,
                //                "<Дата окончания>" -> (dateFormatter.format(employmentOrder.end_date.get).toString),
                "<Дата приема>" -> (dateFormatter.format(employmentOrder.start_date).toString),
                "<Номер документа>" -> employmentOrder.id.toString,
                "<Дата документа>" -> (dateFormatter.format(employmentOrder.date_of_order).toString)
              )
              keywords.foreach {
                case (key, value) => docx.getRange().replaceText(key, value)
              }

              var file = new File("tmp/" + UUID.randomUUID().toString + ".doc")
              var out = new FileOutputStream(file)
              docx.write(out)
              Ok.sendFile(file)
            }.getOrElse(NotFound("Указанный в данном приказе сотрудник не найден в базе."))
          }
          case _ => NotFound("Не указан путь к файлу шаблона, данного типа договора.")
        }
      }.getOrElse(NotFound("В данном приказе не указан тип договора."))
    }.getOrElse(NotFound("Не найдент приказ с таким номером."))
  }

  def getEmploymentOrderContract(employmentOrderId: Long) = Action {
    EmploymentOrder.findById(employmentOrderId).map { employmentOrder =>
      ContractType.findById(employmentOrder.contract_type_id).map { contractType =>
        contractType.file_path match {
          case Some(filePath) => {
            Employee.findById(employmentOrder.employee_id).map { employee =>
              Passport.findEmployeePassport(employee.id).map { passport =>
                ContactInformation.findEmployeeInformation(employee.id).map { contact_information =>
                  val fs = new POIFSFileSystem(new FileInputStream(filePath))
                  var doc = new HWPFDocument(fs)
                  val dateFormatter = new SimpleDateFormat("dd.MM.yyyy")

                  val position = Structure.findById(employmentOrder.position_id).get

                  val department = Structure.findById(position.parent_id.get).get

                  val keywords = Map(
                    "$СОТРУДНИК.ИМЯ" -> employee.firstname,
                    "$СОТРУДНИК.ФАМИЛИЯ" -> employee.surname,
                    "$СОТРУДНИК.ОТЧЕСТВО" -> employee.lastname,
                    "$СОТРУДНИК.АДРЕС.РЕГИСТРАЦИИ" -> passport.reg_address,
                    "$СОТРУДНИК.АДРЕС.ПРОЖИВАНИЯ" -> contact_information.living_address,
                    "$ПАСПОРТ.СЕРИЯ" -> passport.serial,
                    "$ПАСПОРТ.ДАТА.ВЫДАЧИ" -> (dateFormatter.format(passport.open_date).toString + " г."),
                    "$ПАСПОРТ.ДАТА.ОКОНЧАНИЯ" -> (dateFormatter.format(passport.end_date).toString + " г."),
                    "$ДОГОВОР.ДАТА.НАЧАЛА" -> (dateFormatter.format(employmentOrder.start_date).toString),
                    "$ДОГОВОР.ДАТА.КОНЕЦ" -> "",
                    "$РУКОВОДИТЕЛЬ.ФИО" -> "Бапа уулу Кубанычбек",
                    "$РУКОВОДИТЕЛЬ.ДОЛЖНОСТЬ" -> "Председатель правления",
                    "$СОТРУДНИК.ДОЛЖНОСТЬ" -> position.fullname,
                    "$СОТРУДНИК.ОТДЕЛ" -> department.fullname
                  )
                  keywords.foreach {
                    case (key, value) => doc.getRange().replaceText(key, value)
                  }
                  //    saveWord(filePath, doc)
                  var file = new File("test.doc")
                  var out = new FileOutputStream(file)
                  doc.write(out)
                  Ok.sendFile(file)
                }.getOrElse(NotFound(s"Не указаные контактные данные сотрудника ${employee.surname} ${employee.firstname}."))
              }.getOrElse(NotFound(s"Для сотрудника ${employee.surname} ${employee.firstname} не указаны паспортные данные."))
            }.getOrElse(NotFound("Указанный в данном приказе сотрудник не найден в базе."))
          }
          case _ => NotFound("Не указан путь к файлу шаблона, данного типа договора.")
        }
      }.getOrElse(NotFound("В данном приказе не указан тип договора."))
    }.getOrElse(NotFound("Не найдент такой приказ."))
  }

  def comet = Action {
    val events = Enumerator(
      """<script>console.log('kiki')</script>""",
      """<script>console.log('foo')</script>""",
      """<script>console.log('bar')</script>"""
    )
    Ok.stream(events >>> Enumerator.eof).as(HTML)
  }

  def saveEmployment = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[EmploymentOrder].fold(
      valid = { order =>
        val newOrder = order.save

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
              end_date = new_order.end_date
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
          (maxElement, element) => if (maxElement.start_date.isAfter(element.start_date)) maxElement else element
        } match {
          case Some(position) => position.copy(close_date = Some(newOrder.leaving_date), dismissal_order_id = Some(newOrder.id)).update
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


  def createTransfer = Action { implicit request =>
    Ok(views.html.order.transfer.create())
  }

  def listTransfer = Action { implicit request =>
    Ok(views.html.order.transfer.list())
  }

  def jsonTransferList() = Action { implicit request =>
    case class TransferOrderFull(order: TransferOrder, employee: Employee, calendarType: CalendarType, contractType: ContractType, position: Structure)
    implicit val testWrites: Writes[TransferOrderFull] = (
      (JsPath \ "order").write[TransferOrder] and
        (JsPath \ "employee").write[Employee] and
        (JsPath \ "calendarType").write[CalendarType] and
        (JsPath \ "contractType").write[ContractType] and
        (JsPath \ "position").write[Structure]
      )(unlift(TransferOrderFull.unapply))

    implicit val testReads: Reads[TransferOrderFull] = (
      (JsPath \ "order").read[TransferOrder] and
        (JsPath \ "employee").read[Employee] and
        (JsPath \ "calendarType").read[CalendarType] and
        (JsPath \ "contractType").read[ContractType] and
        (JsPath \ "position").read[Structure]
      )(TransferOrderFull.apply _)

    val employees = TransferOrder.findFull.map { order => Json.toJson(TransferOrderFull(order._1, order._2, order._3, order._4, order._5))}
    Ok(Json.toJson(employees))
  }


  def saveTransfer = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[TransferOrder].fold(
      valid = { order =>
        Ok(Json.toJson(order.save))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def updateTransfer = Action(parse.json) { implicit request =>
    val orderJson = request.body
    orderJson.validate[TransferOrder].fold(
      valid = { order =>
        order.update.map { new_order =>
          //          Position.findByTransferOrderId(new_order.id).map { position =>
          //            position.copy(position_id = new_order.position_id,
          //              employee_id = new_order.employee_id, start_date = new_order.start_date,
          //              end_date = new_order.end_date, close_date = new_order.close_date
          //            ).update
          //          }
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