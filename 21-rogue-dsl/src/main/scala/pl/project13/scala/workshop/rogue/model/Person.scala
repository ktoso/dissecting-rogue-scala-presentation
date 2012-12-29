package pl.project13.scala.workshop.rogue.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.{MongoListField, IntPk}
import net.liftweb.record.field._
import pl.project13.scala.workshop.mongo.MongoConfig.MainMongoIdentifier

class UglyPerson {
  var firstName: String = _
  var lastName: Option[String] = _
  var age: Int = _
}

//     |
//     |
//  becomes
//     |
//     |
//     V

case class SimplePerson(
  firstName: String,
  lastName: Option[String],
  age: Int
)

//     |
//     |
//  becomes
//     |
//     |
//     V



class Person extends MongoRecord[Person] with IntPk[Person] {
  override val meta = Person

  throw new RuntimeException("Not implemented yet!") // TODO implement me

  object firstName
  object lastName
  object age

}



object Person extends Person with MongoMetaRecord[Person] {

  override def mongoIdentifier = MainMongoIdentifier
  override def collectionName = "persons"

  def ensureIndexes() {
    throw new RuntimeException("Not implemented yet!") // TODO implement me
    // todo show ensureIndex()
  }

  def findByName(name: String) =
    throw new RuntimeException("Not implemented yet!") // TODO implement me

}
