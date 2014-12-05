package models

import java.time.LocalDate

import models.Database.{MyLocalDate, TimeStamp}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Query
import org.squeryl.annotations.Column
import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._


case class CalendarType(
                         id: Int,
                         name: String,
                         override var created_at: TimeStamp,
                         override var updated_at: TimeStamp
                         ) extends Entity[Int] {

  override def save = inTransaction {
    super.save.asInstanceOf[CalendarType]
  }

  def update = inTransaction {
    CalendarType.findById(this.id).map { calendarType =>
      val tmp = this.copy(created_at = calendarType.created_at, updated_at = calendarType.updated_at)
      tmp.save
    }
  }

}

object CalendarType {

  import Database.calendarTypeTable
  import Database.formatMyLocalDate

  implicit val calendarTypeWrites: Writes[CalendarType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(CalendarType.unapply))

  implicit val calendarTypeReads: Reads[CalendarType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(CalendarType.apply _)

  def allQ: Query[CalendarType] = from(calendarTypeTable) {
    calendarType => select(calendarType)
  }

  def findAll: Iterable[CalendarType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(calendarTypeTable) {
      calendarType => where(calendarType.id === id) select (calendarType)
    }.headOption
  }

}

case class Calendar(
                     id: Long,
                     calendar_type: Int,
                     calendar_date: MyLocalDate,
                     day_type: Int,
                     override var created_at: TimeStamp,
                     override var updated_at: TimeStamp
                     ) extends Entity[Long] {

  override def save = inTransaction {
    super.save.asInstanceOf[Calendar]
  }

  def update = inTransaction {
    Calendar.findById(this.id).map { calendar =>
      val tmp = this.copy(created_at = calendar.created_at, updated_at = calendar.updated_at)
      tmp.save
    }
  }
}

object Calendar {

  import Database.calendarTable

  implicit val calendarWrites: Writes[Calendar] = (
    (JsPath \ "id").write[Long] and
      (JsPath \ "calendar_type").write[Int] and
      (JsPath \ "calendar_date").write[MyLocalDate] and
      (JsPath \ "day_type").write[Int] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(Calendar.unapply))

  implicit val calendarReads: Reads[Calendar] = (
    (JsPath \ "id").read[Long] and
      (JsPath \ "calendar_type").read[Int] and
      (JsPath \ "calendar_date").read[MyLocalDate] and
      (JsPath \ "day_type").read[Int] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(Calendar.apply _)

  def allQ: Query[Calendar] = from(calendarTable) {
    calendar => select(calendar)
  }

  def findAll: Iterable[Calendar] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(calendarTable) {
      calendar => where(calendar.id === id) select (calendar)
    }.headOption
  }

}

case class DayType(
                    id: Int,
                    name: String,
                    hours: Int,
                    @Column("type")
                    dayType: Boolean,
                    override var created_at: TimeStamp,
                    override var updated_at: TimeStamp
                    ) extends Entity[Int] {

  override def save = inTransaction {
    super.save.asInstanceOf[DayType]
  }

  def update = inTransaction {
    DayType.findById(this.id).map { dayType =>
      val tmp = this.copy(created_at = dayType.created_at, updated_at = dayType.updated_at)
      tmp.save
    }
  }

}

object DayType {

  import Database.dayTypeTable

  implicit val dayTypeWrites: Writes[DayType] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "name").write[String] and
      (JsPath \ "hours").write[Int] and
      (JsPath \ "dayType").write[Boolean] and
      (JsPath \ "created_at").write[TimeStamp] and
      (JsPath \ "updated_at").write[TimeStamp]
    )(unlift(DayType.unapply))

  implicit val dayTypeReads: Reads[DayType] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "hours").read[Int] and
      (JsPath \ "dayType").read[Boolean] and
      (JsPath \ "created_at").read[TimeStamp] and
      (JsPath \ "updated_at").read[TimeStamp]
    )(DayType.apply _)

  def allQ: Query[DayType] = from(dayTypeTable) {
    calendar => select(calendar)
  }

  def findAll: Iterable[DayType] = inTransaction {
    allQ.toList
  }

  def findById(id: Long) = inTransaction {
    from(dayTypeTable) {
      dayType => where(dayType.id === id) select (dayType)
    }.headOption
  }

}