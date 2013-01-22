package pl.project13.scala.workshop.rogue.ourown

import org.scalatest.matchers.ShouldMatchers
import pl.project13.scala.workshop.mongo.MongoSpec
import pl.project13.scala.workshop.rogue.model.Person
import org.scalatest.BeforeAndAfter

class OwnRogueDSLTest extends MongoSpec with ShouldMatchers
  with BeforeAndAfter {

  import OwnRogueDSL._

  before {
    registerCreatedRecords {
      Person.createRecord.firstName("Craig").lastName("Cracker").age(2).save(true) ::
      Person.createRecord.firstName("Ted")  .lastName("Type")   .age(3).save(true) ::
      Person.createRecord.firstName("Henry").lastName("Hacker") .age(5).save(true) :: Nil
    }
  }

  it should "query for people by name" in {
    val people = Person where(_.firstName eqs "Ted") findAll()

    // what's the type here?
    people.map(_.lastName.get) should contain ("Type")       // todo hey, wont compile! And the compiler is right :-)
  }

  it should "add QueryBuilders to StringFields" in {
    Person.firstName eqs "Bob"
  }

  it should "find all people" in {
    val people = Person findAll()

    people should have length (3)
  }

  it should "find a person by its name" in {
    val henry = Person where(_.firstName eqs "Henry") findOne()

    henry.get.lastName.get should equal ("Hacker")       // todo notice the nice error message
  }

}
