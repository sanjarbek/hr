package models

import java.io.{FileOutputStream, File, FileInputStream}
import java.util.{Calendar, Date}

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable

case class ContractType(
                         id: Int,
                         name: String,
                         file_path: Option[String]
                         ) extends KeyedEntity[Int]

object ContractType {

  import Database.{contractTypeTable}

  implicit val contractTypeWrites: Writes[ContractType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "file_path").write[Option[String]]
    )(unlift(ContractType.unapply))

  implicit val contractTypeReads: Reads[ContractType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](50)) and
      (JsPath \ "file_path").read[Option[String]]
    )(ContractType.apply _)

  def allQ: Query[ContractType] = from(contractTypeTable) {
    contractType => select(contractType)
  }

  def findAll: Iterable[ContractType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(contractTypeTable) {
      contractType => where(contractType.id === id) select (contractType)
    }.headOption
  }

  def insert(contractType: ContractType): ContractType = inTransaction {
    contractTypeTable.insert(contractType)
  }

  def update(contractType: ContractType) {
    inTransaction {
      contractTypeTable.update(contractType)
    }
  }

  def delete(id: Long) = inTransaction {
    contractTypeTable.deleteWhere(contractType => contractType.id === id)
  }

  def template = {
    val filePath = "C:\\Users\\samatov\\IdeaProjects\\playbasics\\PlayBasics\\hr\\documents\\work_contract_limited.doc"
    filePath
  }
}
