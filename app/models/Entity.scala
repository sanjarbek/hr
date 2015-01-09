package models

import java.sql.Timestamp
import java.time.{ZoneId, LocalDateTime}
import org.squeryl.{KeyedEntityDef, KeyedEntity, View}
import MyCustomTypes._
import play.api.Logger


trait Model[K] extends KeyedEntity[K] {

  def id: K

  lazy val table = Database.findTablesFor(this).head

  implicit def modelKED[Key] = new KeyedEntityDef[Model[Key], Key] {
    def getId(a: Model[Key]) = a.id

    def isPersisted(a: Model[Key]) = a.id.toString.toLong > 0

    def idPropertyName = "id"

    override def optimisticCounterPropertyName = Some("occVersionNumber")
  }

  def save = {
    table.insertOrUpdate(this)
  }

}

trait Entity[K] extends Model[K] with Product {

  var created_at: LocalDateTime
  var updated_at: LocalDateTime

  override def save = {
    if (!isPersisted) {
      val now = LocalDateTime.now
      updated_at = now
      created_at = now
    }
    else {
      updated_at = LocalDateTime.now
    }

    super.save
  }
}
