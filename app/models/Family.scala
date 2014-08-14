package models

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Query, KeyedEntity}
import org.squeryl.Table
import org.squeryl._
import org.squeryl.Query
import collection.Iterable

case class Family(
                     id: Long,
                     employee_id: Long,
                     relationship: Int,
                     surname: String,
                     firstname: String,
                     lastname: String,
                     birthday: Date
) extends KeyedEntity[Long]

object Family {
  import Database.{familyTable}

  def allQ: Query[Family] = from(familyTable) {
    family => select(family)
  }

  def findAll: Iterable[Family] = inTransaction{
    allQ.toList
  }

  def findEmployeeFamily(employeeId: Long) = inTransaction{
    from(allQ) { family =>
      where(family.employee_id===employeeId) select(family)
    }.toList
  }

  def findById(id: Long) = inTransaction{
    from(familyTable) {
      family => where(family.id === id) select(family)
    }.headOption
  }

  def insert(family: Family): Family = inTransaction{
    familyTable.insert(family)
  }

  def update(family: Family) {
    inTransaction{ familyTable.update(family)}
  }
}
