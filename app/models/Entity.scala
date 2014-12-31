package models

import java.time.LocalDateTime

import models.Database.TimeStamp
import org.joda.time.DateTime
import org.squeryl.{KeyedEntity}
import org.squeryl.View
import org.squeryl.PrimitiveTypeMode._
import play.api.Logger


trait Model[K] extends KeyedEntity[K] {

  def id: K

  lazy val table = Database.findTablesFor(this).head

  override def isPersisted() = this.id.toString.toLong > 0

  def save = {
    table.insertOrUpdate(this)
  }

}

trait Entity[K] extends Model[K] with Product {

  var created_at: TimeStamp
  var updated_at: TimeStamp

  override def save = {
    if (!isPersisted()) {
      val now = LocalDateTime.now()
      updated_at = now
      created_at = now
    }
    else {
      updated_at = LocalDateTime.now()
    }

    super.save
  }
}
