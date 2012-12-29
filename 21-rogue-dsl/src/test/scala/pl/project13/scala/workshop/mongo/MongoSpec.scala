package pl.project13.scala.workshop.mongo

import net.liftweb.mongodb.record.MongoRecord
import scala.collection._
import org.scalatest.{BeforeAndAfterEach, FlatSpec, BeforeAndAfterAll}

trait MongoSpec extends FlatSpec
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with RemoveSafely {

  def verboseMongoRunner = false

  val createdRecords = mutable.HashSet[MongoRecord[_]]()
  var mongoRunner: MongoRunner = _

  override protected def beforeAll() {
    mongoRunner = MongoRunner.run()
    MongoInit.init(TestMongoConfig, verboseMongoRunner)
  }

  /**
   * Register a record to be cleaned up after the test
   */
  def registerCreatedRecord[T](createRecord: => T): T = {
    val record = createRecord
    createdRecords += record.asInstanceOf[MongoRecord[_]]
    record
  }

  /**
   * Register a record to be cleaned up after the test
   */
  def registerCreatedRecords[T](createRecords: => List[T]): List[T] = {
    val records = createRecords
    createdRecords ++= records.asInstanceOf[Seq[MongoRecord[_]]]
    records.toList
  }

  override protected def afterEach() {
    super.afterEach()
    deleteIterableSafely(createdRecords.toIterable)
    createdRecords.clear()
  }

  override protected def afterAll() {
    mongoRunner.stop()
    mongoRunner = null
  }
}
