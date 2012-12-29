// dependency for scalaslide
libraryDependencies += "com.tristanhunt" %% "knockoff" % "0.8.0-16"

// for MongoSpec
SettingKey[File]("mongo-directory") := file("/usr/local/Cellar/mongodb/2.2.0-x86_64")
