package pl.project13.scala.workshop.mongo

import net.liftweb.mongodb.MongoIdentifier
import java.util.Properties

trait MongoConfig {

  protected def rootConfig: Properties

  lazy val mongoServers = Option(rootConfig.getProperty("servers")).getOrElse("localhost")
  lazy val mongoDatabase = Option(rootConfig.getProperty("database")).getOrElse("scala-workshop")

  lazy val printServersAtStartup = true
}

object MongoConfig {

  case object MainMongoIdentifier extends MongoIdentifier {
    override val jndiName = "main_mongo_db"
  }

  def fromAkkaConfig(theRootConfig: Properties): MongoConfig = new MongoConfig {
    override val rootConfig: Properties = theRootConfig
  }
}
