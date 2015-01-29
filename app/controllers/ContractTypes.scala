package controllers

import java.io.{FileOutputStream, File, FileInputStream}
import java.util.Calendar

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import play.api.mvc._
import models.{ContractType}
import play.api.libs.json._

object ContractTypes extends Controller with Security {

  def list = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.contract_type.list())
  }

  def jsonList = HasToken() { _ => currentId => implicit request =>
    val contract_types = ContractType.findAll.map { contract_type => Json.toJson(contract_type)}
    Ok(Json.toJson(contract_types))
  }

  def create = HasToken() { _ => currentId => implicit request =>
    Ok(views.html.contract_type.create())
  }

  def save = HasToken(parse.json) { _ => currentId => implicit request =>
    val contractTypeJson = request.body
    contractTypeJson.validate[ContractType].fold(
      valid = { contract_type =>
        val ret = ContractType.insert(contract_type)
        Ok(Json.toJson(ret))
      },
      invalid = { errors =>
        BadRequest(JsError.toFlatJson(errors))
      }
    )
  }

  def delete(id: Long) = HasToken() { _ => currentId => implicit request =>
    ContractType.delete(id)
    Ok(Json.toJson("Removed"))
  }

  def upload = HasToken(parse.multipartFormData) { _ => currentId => implicit request =>
    val id = request.body.dataParts("id")(0).toLong
    ContractType.findById(id).map { contract_type =>
      request.body.file("file").map { picture =>
        import java.io.File
        val filename = picture.filename
        ContractType.update(ContractType(contract_type.id, contract_type.name, Option(filename)))
        val contentType = picture.contentType
        picture.ref.moveTo(new File(s"./uploaded/documents/$filename"), true)
        Ok(s"File '$filename' uploaded.")
      }.getOrElse {
        NotAcceptable
      }
    }.getOrElse {
      NotFound
    }
  }

}