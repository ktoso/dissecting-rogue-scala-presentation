package pl.project13.scala.workshop.mongo

import collection.JavaConversions
import com.mongodb.{Mongo, ServerAddress}
import MongoConfig._
import net.liftweb.mongodb.{MongoIdentifier, MongoDB}
import pl.project13.scala.workshop.rogue.model.Person

/**
* Used to establish an MongoDB connection and also ensure all indexes are set.
*/
object MongoInit {

  /**
   * Initializes Lift's Mongo-Record using the provided configuration, and the default mongo identifier.
   */
  def init(config: MongoConfig, verbose: Boolean = true) {
    defineDb(MainMongoIdentifier, config.mongoServers, config.mongoDatabase, config.printServersAtStartup && verbose)

    ensureIndexes()
  }

  def defineDb(mongoIdentifier: MongoIdentifier, servers: String, dbName: String, printDatabases: Boolean = false) {
    val serverList = asServerAdresses(servers)

    if(printDatabases)
      println("Defining [%s]: servers: %s, database name: [%s]".format(mongoIdentifier, serverList, dbName))

    MongoDB.defineDb(mongoIdentifier, createMongo(serverList), dbName)
  }

  def createMongo(serverList: List[ServerAddress]) = {
    // We need to use a different constructor if there's only 1 server to avoid startup exceptions where mongo thinks
    // it's in a replica set.
    if (serverList.size == 1) {
      new Mongo(serverList.head)
    } else {
      import JavaConversions._
      new Mongo(serverList)
    }
  }

  def ensureIndexes() {
    Person.ensureIndexes()
  }

  def asServerAdresses(servers: String): scala.List[ServerAddress] = {
    servers.split(",").map(new ServerAddress(_)).toList
  }
}
