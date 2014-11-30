package models

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
    if (isPersisted) {
      table.update(this)
      this
    }
    else {
      table.insert(this)
    }

  }

}

trait Entity[K] extends Model[K] with Product {

  var created_at: TimeStamp
  var updated_at: TimeStamp

  override def save = {
    if (!isPersisted()) created_at = DateTime.now()
    updated_at = DateTime.now()

    super.save
  }
}
