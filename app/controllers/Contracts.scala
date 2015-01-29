package controllers

import java.io.{FileOutputStream, File, FileInputStream}
import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import play.api.mvc._
import models._
import play.api.libs.json._
import sun.util.calendar.LocalGregorianCalendar.Date

object Contracts extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.contract.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val contracts = Contract.findAll.map { contract => Json.toJson(contract)}
    Ok(Json.toJson(contracts))
  }

  def jsonEmployeeContractsList(id: Long) = HasToken() { _ => currentId => implicit request =>
    val contracts = Contract.findAllByEmployeeId(id).map { contract => Json.toJson(contract)}
    Ok(Json.toJson(contracts))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.contract.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val contractJson = request.body
    contractJson.validate[Contract].fold(
      valid = { contract =>
        val ret = Contract.insert(contract)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    Contract.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def show = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.contract.show())
  }

  def download(id: Long) = HasToken() { _ => currentId => implicit request =>
    Contract.findById(id).map { contract =>
      ContractType.findById(contract.contract_type).map { contract_type =>
        contract_type.file_path.map { filePath =>
          val fs = new POIFSFileSystem(new FileInputStream(s"./uploaded/documents/$filePath"))
          val doc = new HWPFDocument(fs)

          val employee = Employee.findById(contract.employee_id).get
          Passport.findEmployeePassport(employee.id).map { passport =>
            val job = Structure.findById(contract.position_id).get
            val department = Structure.findById(job.parent_id.get).get

            val keywords = Map(
              "$СОТРУДНИК.ИМЯ" -> employee.firstname,
              "$СОТРУДНИК.ФАМИЛИЯ" -> s"${employee.surname}",
              "$СОТРУДНИК.ОТЧЕСТВО" -> employee.lastname,
              "$СОТРУДНИК.АДРЕС.ПРОЖИВАНИЯ" -> "г. Бишкек, пр. Чуй 57/12",
              "$СОТРУДНИК.АДРЕС.РЕГИСТРАЦИИ" -> "г. Бишкек, пр. Чуй 57/12",
              "$СОТРУДНИК.ОТДЕЛ" -> department.name,
              "$СОТРУДНИК.ДОЛЖНОСТЬ" -> job.name,
              "$ПАСПОРТ.СЕРИЯ" -> (passport.serial + " " + passport.number),
              "$ПАСПОРТ.ДАТА.ВЫДАЧИ" -> new SimpleDateFormat("dd.MM.yyyy").format(passport.open_date).toString,
              "$ПАСПОРТ.ДАТА.ОКОНЧАНИЯ" -> new SimpleDateFormat("dd.MM.yyyy").format(passport.end_date).toString,
              "$ДОГОВОР.ДАТА.НАЧАЛА" -> new SimpleDateFormat("dd MMMM yyyy").format(contract.open_date).toString,
              "$ДОГОВОР.ДАТА.ОКОНЧАНИЯ" -> new SimpleDateFormat("dd MMMM yyyy").format(contract.end_date).toString,
              "$ДОГОВОР.ИСПЫТАТЕЛЬНЫЙ.СРОК.НАЧАЛА" -> new SimpleDateFormat("dd MMMM yyyy").format(contract.trial_period_open.get).toString,
              "$ДОГОВОР.ИСПЫТАТЕЛЬНЫЙ.СРОК.ОКОНЧАНИЯ" -> new SimpleDateFormat("dd MMMM yyyy").format(contract.trial_period_end.get).toString,
              "$ДОГОВОР.ОКЛАД" -> contract.salary.toString(),
              "$РУКОВОДИТЕЛЬ.ДОЛЖНОСТЬ" -> "Председатель Правления",
              "$РУКОВОДИТЕЛЬ.ФИО" -> "Бапа уулу Кубанычбек"
            )
            keywords.foreach {
              case (key, value) => doc.getRange().replaceText(key, value)
            }

            var file = new File("test.doc")
            var out = new FileOutputStream(file)
            doc.write(out)
            Ok.sendFile(file)
          }.getOrElse(NotFound("Заполните паспортные данные сотрудника."))
        }.getOrElse(NotFound("Не найден шаблон."))
      }.getOrElse(NotFound("Не установлен вид шаблона."))
    }.getOrElse(NotFound("Не найден договор."))

  }


}