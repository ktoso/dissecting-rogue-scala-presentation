package pl.project13.scala.workshop.rogue.ourown

import net.liftweb.mongodb.record._
import field.MongoListField
import net.liftweb.record.field._
import net.liftweb.record._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.mongodb.MongoDB
import com.mongodb.BasicDBObject
import com.mongodb.casbah.commons.ValidBSONType.DBObject
import org.bson.BasicBSONObject

trait OwnRogueDSL extends WhereDSL with QueryFieldDSL

object OwnRogueDSL extends OwnRogueDSL

trait WhereDSL {

  implicit def addRogueDSL[M <: MongoRecord[M]](meta: MongoMetaRecord[M]) =
    new OwnRogueQueryBuilder(meta)(Nil)
}

trait QueryFieldDSL {

  implicit def addStringFieldQueryClause[Owner <: MongoRecord[Owner]](field: StringField[Owner]) =
    StringFieldClauses(field)

  implicit def addIntFieldQueryClause[Owner <: MongoRecord[Owner]](field: IntField[Owner]) =
    IntFieldClauses(field)
}

trait QueryField { def field: BaseField }

trait DefaultQueryField extends QueryField {
  def eqs(value: String) = WhereQueryClause(field.name, value)
  def neqs(value: String) = WhereQueryClause(field.name, "neqs", value)
}

trait NumericQueryField extends QueryField {
  def gt(value: String)  = WhereQueryClause(field.name, "gt",  value)
  def gte(value: String) = WhereQueryClause(field.name, "gte", value)
  def lt(value: String)  = WhereQueryClause(field.name, "lt",  value)
  def lte(value: String) = WhereQueryClause(field.name, "lte", value)
}

case class StringFieldClauses(override val field: BaseField) extends DefaultQueryField
case class    IntFieldClauses(override val field: BaseField) extends DefaultQueryField with NumericQueryField

class OwnRogueQueryBuilder[Owner <: MongoRecord[Owner]]
                          (meta: MongoMetaRecord[Owner])
                          (val whereParts: List[QueryClause] = Nil) {

  def where(queryOn: Owner => QueryClause) =
    new OwnRogueQueryBuilder(meta)(whereParts = queryOn(meta.createRecord) :: whereParts)

  def findAll(): List[Owner] = {
    MongoDB.useCollection(meta.mongoIdentifier, meta.collectionName) { db =>
      val queryObject = new BasicDBObject()
      whereParts.foreach { clause => queryObject.put(clause.key, clause.value) }
      val cursor = db.find(queryObject)

      import collection.JavaConversions._
      cursor.iterator.map(meta.fromDBObject).toList
    }
  }

  @inline def get(): Option[Owner] = findOne()

  def findOne(): Option[Owner] = {
    MongoDB.useCollection(meta.mongoIdentifier, meta.collectionName) { db =>
      val queryObject = new BasicDBObject()
      whereParts.foreach { clause => queryObject.put(clause.key, clause.value) }
      val cursor = db.find(queryObject)

      import collection.JavaConversions._
      cursor.headOption.map(meta.fromDBObject)
    }
  }
}

case class QueryClause(key: String, value: Any)

object WhereQueryClause {

  def apply(field: String, value: String) =
    new QueryClause(field, value)

  def apply(field: String, value: Number) =
    new QueryClause(field, value.toString)

  def apply(field: String, operator: String, value: String) =
    new QueryClause(field, (operator -> value).toString)

  def apply(field: String, operator: String, value: Number) =
    new QueryClause(field, (operator -> value.toString).toString)
}