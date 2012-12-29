package pl.project13.scala.workshop.mongo

import java.util.Properties

object TestMongoConfig extends MongoConfig {
  override val rootConfig = {
    val props = new Properties()
    props.put("servers", "localhost:" + MongoRunner.NonStandardMongoPort)
    props.put("database", "scala-workshop")
    props
  }
}
