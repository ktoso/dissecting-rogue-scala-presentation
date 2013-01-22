package pl.project13.scala.workshop.rogue.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.IntPk
import net.liftweb.record.field._
import pl.project13.scala.workshop.mongo.MongoConfig.MainMongoIdentifier

class UglyPerson {
  var firstName: String = _
  var lastName: Option[String] = _
  var age: Int = _
}

//     |
//  becomes
//     |
//     V

case class SimplePerson(
  firstName: String,
  lastName: Option[String],
  age: Int
)

//     |
//  becomes
//     |
//     V

class Person extends MongoRecord[Person] with IntPk[Person] {
  override val meta = Person

  object firstName extends StringField[Person](this, 255)
  object lastName  extends StringField[Person](this, 255)
  object age       extends IntField[Person](this)

}

object Person extends Person with MongoMetaRecord[Person] {

  override def mongoIdentifier = MainMongoIdentifier
  override def collectionName = "persons"

  def ensureIndexes() {
    import net.liftweb.json.JsonDSL._
    ensureIndex((firstName.name -> 1) ~ (lastName.name -> 1))
  }

  def byName(name: String): List[Person] = {
    import pl.project13.scala.workshop.rogue.ourown.OwnRogueDSL._
    meta where(_.firstName eqs name) findAll()
  }

}
