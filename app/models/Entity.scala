package models

import models.Database.TimeStamp
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity}
import org.squeryl.View
import org.squeryl.PrimitiveTypeMode._


trait Model extends KeyedEntity[Long] {
  def id: Long

  lazy val table = Database.findTablesFor(this).head

  implicit def modelKED = {
    def getId(a: Model) = a.id

    def idPropertyName = "id"
  }

  def save = table.insertOrUpdate(this)

}

trait Entity extends Model with Product {

  var created_at: TimeStamp = null
  var updated_at: TimeStamp = null

  override def save = {
    if (created_at == null) created_at = DateTime.now()
    updated_at = DateTime.now()
    super.save
  }
}
