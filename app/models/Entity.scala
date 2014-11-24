package models

import models.Database.TimeStamp
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity}
import org.squeryl.View
import org.squeryl.PrimitiveTypeMode._


trait Model[K] extends KeyedEntity[K] {
  def id: K

  lazy val table = Database.findTablesFor(this).head

  implicit def modelKED[Key] = {
    def getId(a: Model[Key]) = a.id

    def isPersisted(a: Model[Key]) = a.id.toString.toLong > 0L

    def idPropertyName = "id"
  }

  def save = table.insertOrUpdate(this)

}

trait Entity extends Model[Long] with Product {
  val id = 0L
  var created_at: TimeStamp = null
  var updated_at: TimeStamp = null

  override def save = {
    if (created_at == null) created_at = DateTime.now()
    updated_at = DateTime.now()
    super.save
  }
}



