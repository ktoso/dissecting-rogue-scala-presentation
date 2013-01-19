import sbt._
import sbt.Keys._
import pl.project13.scalaslide.ScalaSlideKeys._
import pl.project13.scalaslide.ScalaSlideTasks._

object Resolvers {
  val smsserResolvers = Seq(
    "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
    "EasyTesting Releases" at "http://repo1.maven.org/maven2/org/easytesting"
  )
}

object Versions {
  val knockoff = "0.8.0-16"
  val guava = "12.0"
  val rainbow = "0.1"

  val mockito = "1.8.5"
  val scalatest = "2.0.M5-B1"

  val fest = "1.4"
  val lift = "2.5-M4"
}

object Dependencies {
  import Resolvers._
  import Versions._

  // mongodb and related
  val liftJson              = "net.liftweb"            %% "lift-json"             % Versions.lift
  val casbah                = "org.mongodb"            %% "casbah-core"           % "2.5.0"
  val mongo                 = "org.mongodb"            %  "mongo-java-driver"     % "2.9.2"
  val liftMongoRecord       = "net.liftweb"            %% "lift-mongodb-record"   % Versions.lift withSources()
  val rogue                 = "com.foursquare"         %% "rogue-lift"            % "2.0.0-beta22" intransitive() withSources()

  val rogueAll = Seq(liftJson, casbah, mongo, rogue, liftMongoRecord)

  // Logging
  val slf4s                 = "com.weiglewilczek.slf4s" %% "slf4s"                % "1.0.7"
  val logback               = "ch.qos.logback"        % "logback-classic"         % "1.0.0"
  val log4jOverSlf4j        = "org.slf4j"             % "log4j-over-slf4j"        % "1.6.1"
  val jclOverSlf4j          = "org.slf4j"             % "jcl-over-slf4j"          % "1.6.1"
  val julToSlf4jBridge      = "org.slf4j"             % "jul-to-slf4j"            % "1.6.1"

  val logging               = Seq(slf4s, logback, log4jOverSlf4j, jclOverSlf4j)

  // general tools
  val scalaToolsTime        = "org.scala-tools.time"  %%  "time"                  % "0.5" intransitive()
  val jodaTime              = "joda-time"             %   "joda-time"             % "2.1"
  val jodaTimeConvert       = "org.joda"              %   "joda-convert"          % "1.2"
  val scalaz                = "org.scalaz"            %% "scalaz-core"            % "6.0.4"
  val guava                   = "com.google.guava"    % "guava"                   % Versions.guava

  // testing
  val scalaTest               = "org.scalatest"       % "scalatest_2.10.0-RC3"    % Versions.scalatest
  val scalaTest_2_9           = "org.scalatest"          %% "scalatest"             % "1.8"
  val mockito                 = "org.mockito"         % "mockito-core"            % Versions.mockito

  val testing_2_10            = Seq(scalaTest, mockito).map(_ % "test")
  val testing_2_9             = Seq(scalaTest_2_9, mockito).map(_ % "test")

  // Akka2
  val akka2Version           = "2.0.3"
  val akka2Actor             = "com.typesafe.akka" % "akka-actor"          % akka2Version
  val akka2ZeroMQ            = "com.typesafe.akka" % "akka-zeromq"         % akka2Version
  val akka2Slf4j             = "com.typesafe.akka" % "akka-slf4j"          % akka2Version
  val akka2TestKit           = "com.typesafe.akka" % "akka-testkit"        % akka2Version % "test"
  val akka2Full              = Seq(akka2Actor, akka2ZeroMQ, akka2Slf4j, akka2TestKit)

  // terminal coloring
  val rainbow                 = "pl.project13.scala"      %% "rainbow"                   % Versions.rainbow

  // java stuff
  val festAssert              = "org.easytesting"          % "fest-assert"               % Versions.fest      % "test"

}

object BuildSettings {
  import Resolvers._
  import Dependencies._

  val dependencies  = Seq(
    guava
  )

  val buildSettings = Defaults.defaultSettings ++
    Seq(
      organization := "pl.krakowscala",
      name         := "dissecting.rogue",
      version      := "1.0",
      scalaVersion := "2.10.0",
      libraryDependencies ++= dependencies
    )

  val mongoDirectory = SettingKey[File]("mongo-directory")

  val mongoSpecSettings = Seq(
    parallelExecution := false, // We are starting mongo in tests.
    testOptions in Test <+= mongoDirectory map {
      md => Tests.Setup{ () =>
        val mongoFile = new File(md.getAbsolutePath + "/bin/mongod")
        if(mongoFile.exists) {
          System.setProperty("mongo.directory", md.getAbsolutePath)
        } else {
          throw new RuntimeException(
            ("""|Unable to find [mongodb] in 'mongo.directory' (%s). Please check your ~/.sbt/local.sbt file.
                |Example: SettingKey[File]("mongo-directory") := file("/usr/local/Cellar/mongodb/2.2.0-x86_64")  """.stripMargin).format(mongoFile.getAbsolutePath))
        }
      }
    }
  )
}

object ScalaWorkshopBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val rogueDsl = Project(
    "rogue-dsl",
    file("21-rogue-dsl"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= testing_2_10 ++ rogueAll
    ) ++ mongoSpecSettings
  )

  lazy val root = Project (
    "root",
    file("."),
    settings = buildSettings ++ scalaslideSettings ++
      Seq (
      )
  ) aggregate (
    rogueDsl
  )

}
