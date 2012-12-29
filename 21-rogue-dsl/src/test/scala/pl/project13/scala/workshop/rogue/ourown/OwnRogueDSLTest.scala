package pl.project13.scala.workshop.rogue.ourown

import org.scalatest.matchers.ShouldMatchers
import pl.project13.scala.workshop.mongo.MongoSpec
import pl.project13.scala.workshop.rogue.model.Person
import org.scalatest.BeforeAndAfter

class OwnRogueDSLTest extends MongoSpec with ShouldMatchers
//  with OwnRogueDSL      // todo why this way?
  with BeforeAndAfter {

//  import OwnRogueDSL._ // todo why this way?

  before {
    registerCreatedRecords {
      Person.createRecord.name("Craig").lastName("Cracker").age(2).save(true) ::
      Person.createRecord.name("Ted")  .lastName("Type")   .age(3).save(true) ::
      Person.createRecord.name("Henry").lastName("Hacker") .age(5).save(true) :: Nil
    }
  }

  it should "query for people by name" in {
//    // todo what's _.firstName?
//    val people = Person where(_.name eqs "Ted").findAll()
//
//    people.isInstanceOf[List[Person]]
//    people.map(_.lastName.get) should contain ("Type")       // todo hey, wont compile! And the compiler is right :-)
    fail("Won't compile yet!")
  }

  it should "add QueryBuilders to StringFields" in {
//    Person.firstName eqs "Bob"                  // todo add eqs method to StringField!
    fail("Won't compile yet!")
  }

  it should "find all people" in {
//    val people = Person findAll()
//
//    people should have length (3)
    fail("Won't compile yet!")
  }

  it should "find a person by its name" in {
//    val henry = Person where(_.firstName eqs "Henry") findOne()
//
//    henry.get.lastName.get should equal ("Hacker")       // todo notice the nice error message
    fail("Won't compile yet!")
  }

}
