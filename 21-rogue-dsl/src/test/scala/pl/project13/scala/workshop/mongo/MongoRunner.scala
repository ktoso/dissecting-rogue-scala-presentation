package pl.project13.scala.workshop.mongo

import java.io.File
import pl.softwaremill.common.util.io.KillableProcess
import com.mongodb.ServerAddress
import java.util.Scanner
import java.util.regex.Pattern
import com.google.common.io.Files

class MongoRunner(process: KillableProcess, temporaryDataDir: File, port: Int, pidFile: String, verbose: Boolean) {

  def serverAddress() = new ServerAddress("localhost", port)

  def stop() {
    val scanner = processOutputScannerForMessage(process.getProcess, "dbexit: really exiting now")
    process.sendSigInt()
    try {
    scanner.next()
    } catch {
      case ex: Exception => println("Unable to next on process scanner", ex)
    }

    if (!deleteDirectory(temporaryDataDir)) {
      throw new RuntimeException("Cannot delete temporary mongo data directory "+temporaryDataDir)
    }

    new File(pidFile).delete()

    if (verbose)
      println("Stopped Mongo on port "+port)
  }

  def processOutputScannerForMessage(process: Process, message: String) = {
    new Scanner(process.getInputStream).useDelimiter(Pattern.quote(message))
  }

  def deleteDirectory(directory: File) = {
    Runtime.getRuntime.exec(Array[String](
      "rm",
      "-rf",
      directory.getAbsolutePath)).waitFor() == 0
  }
}

object MongoRunner {
  val NonStandardMongoPort = 27017 + 89

  def run(verbose: Boolean = false): MongoRunner = run(NonStandardMongoPort, verbose)

  def run(port: Int, verbose: Boolean): MongoRunner = {
    val temporaryDataDir = Files.createTempDir()

    val actualPidFile = new File(pidFile)
    if( actualPidFile.exists() && actualPidFile.length()>0) {
      throw new Exception("Mongo pidfile (with content) already present:\n   '" + pidFile + "'.\nStop the mongo process and it should empty the file:\n\n   kill `cat " + pidFile +"`\n")
    }
    val command = mongoExecutable+" --dbpath="+temporaryDataDir.getAbsolutePath+
      " --port="+port+" --nojournal" +
      " --pidfilepath " + pidFile
    val killableProcess = new KillableProcess(command, "'port="+port+"'")
    killableProcess.start()

    if (verbose)
      println("Started Mongo on port "+port+", db path "+temporaryDataDir)

    val mongoRunner = new MongoRunner(killableProcess, temporaryDataDir, port, pidFile, verbose = verbose)

    try {
      mongoRunner
        .processOutputScannerForMessage(killableProcess.getProcess, "waiting for connections on port "+port)
        .next()
    } catch {
      case element:NoSuchElementException =>
        println("Could not start " + command)
        throw element
    }

    mongoRunner
  }

  def mongoExecutable = List(mongoDirectory, "bin", "mongod").mkString(File.separator)

  def pidFile = List(mongoDirectory,"mongo.scalatest.pid").mkString(File.separator)

  def mongoDirectory = {
    val varName = "mongo.directory"

    val mongoDir = System.getProperty(varName)
    if (mongoDir == null) {
      throw new Exception("You must set the [%s] system property".format(varName))
    }
    if ( !new File(mongoDir).exists() ) {
      throw new Exception("Mongo directory %s does not exist".format(mongoDir))
    }
    mongoDir
  }
}
