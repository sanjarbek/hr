package models

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Column
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import collection.Iterable

case class Structure(
                      id: Int,
                      parent_id: Option[Int],
                      name: String,
                      fullname: String,
                      salary: Float,
                      coefficient: Float,
                      structure_type: Int,
                      position_type: Option[Int],
                      @Column("position_history_id") val positionHistoryId: Option[Long],
                      status: Int
                      ) extends KeyedEntity[Int] {
  def contract: Option[Contract] = {
    Contract.findByStructurePosition(this.id)
  }
}

object Structure {

  import Database.{structureTable, contractTable}

  implicit val structureWrites: Writes[Structure] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "parent_id").write[Option[Int]] and
      (JsPath \ "name").write[String] and
      (JsPath \ "fullname").write[String] and
      (JsPath \ "salary").write[Float] and
      (JsPath \ "coefficient").write[Float] and
      (JsPath \ "structure_type").write[Int] and
      (JsPath \ "position_type").write[Option[Int]] and
      (JsPath \ "positionHistoryId").write[Option[Long]] and
      (JsPath \ "status").write[Int]
    )(unlift(Structure.unapply))

  implicit val structureReads: Reads[Structure] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "parent_id").read[Option[Int]] and
      (JsPath \ "name").read[String](minLength[String](2) keepAnd maxLength[String](50)) and
      (JsPath \ "fullname").read[String](minLength[String](2) keepAnd maxLength[String](250)) and
      (JsPath \ "salary").read[Float] and
      (JsPath \ "coefficient").read[Float] and
      (JsPath \ "structure_type").read[Int] and
      (JsPath \ "position_type").read[Option[Int]] and
      (JsPath \ "positionHistoryId").read[Option[Long]] and
      (JsPath \ "status").read[Int]
    )(Structure.apply _)


  def allQ: Query[Structure] = from(structureTable) {
    office => select(office) orderBy(office.id asc, office.parent_id asc)
  }

  def findAll: Iterable[Structure] = inTransaction {
    allQ.toList
  }

  def findFreePositions = inTransaction {
    from(structureTable)(structure =>
      where(structure.positionHistoryId.isNull) select (structure) orderBy (structure.id asc)
    ).toList
  }

  def findById(id: Long) = inTransaction {
    from(structureTable) {
      structure => where(structure.id === id) select (structure)
    }.headOption
  }

  def insert(structure: Structure): Structure = inTransaction {
    structureTable.insert(structure)
  }

  def update(structure: Structure) {
    inTransaction {
      structureTable.update(structure)
    }
  }

  def delete(id: Long) = inTransaction {
    structureTable.deleteWhere(structure => structure.id === id)
  }

  def findChilds(structureId: Int) = {
    val structures = Structure.findAll.toList
    var results = scala.collection.mutable.ListBuffer[Structure]()

    def scanStructure(id: Int): Unit = {
      for (st <- structures) {
        if (st.parent_id == Some(id)) {
          results += st
          scanStructure(st.id)
        }
      }
    }
    scanStructure(structureId)
    results
  }
}
