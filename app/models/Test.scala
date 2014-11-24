package models

import java.time._
import java.sql.Timestamp
import models.Database.TimeStamp
import org.joda.time.DateTime

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.customtypes.TimestampField
import org.squeryl.{Query, KeyedEntity, Schema}


case class Test(val name: String) extends Entity

object Test {

  import Database.{testTable}

  def findById(id: Long) = inTransaction {
    from(testTable) {
      test => where(test.id === id) select (test)
    }.headOption
  }

  def insert(test: Test): Test = inTransaction {
    test.save.asInstanceOf[Test]
  }

  def delete(id: Long) = inTransaction {
    testTable.deleteWhere(test => test.id === id)
  }
}

