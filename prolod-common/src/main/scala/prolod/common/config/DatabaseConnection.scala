package prolod.common.config

import java.sql.{Connection, DriverManager}
import java.util

import com.ibm.db2.jcc.am.{SqlException, SqlIntegrityConstraintViolationException, SqlSyntaxErrorException}
import com.typesafe.slick.driver.db2.DB2Driver.api._
import prolod.common.models._
import slick.jdbc.{GetResult, StaticQuery => Q}
import scala.collection.mutable
import scala.slick.jdbc.StaticQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.jdbc.JdbcBackend.Session
import scala.util.control.NonFatal
import scala.collection.JavaConverters._
import play.api.libs.json._
import prolod.common.models.PatternFormats.patternDBFormat
import prolod.common.models.{Dataset, Group, Pattern, PatternFromDB}
import slick.profile.SqlStreamingAction

import scala.Function.tupled
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.slick.jdbc.JdbcBackend.Session

/*
case class Schemata(id : String, schema_name : String, entities : Int, tuples : Int)

class Schematas(tag: Tag)
extends Table[Schemata](tag, "PROLOD_MAIN.SCHEMATA") {

def id = column[String]("id", O.PrimaryKey)
def schema_name = column[String]("schema_name", O.NotNull)
def entities = column[Int]("entities", O.NotNull)
def tuples = column[Int]("tuples", O.NotNull)

def * = (id, schema_name, entities, tuples) <> (Schemata.tupled, Schemata.unapply)
}
*/

class DatabaseConnection(config : Configuration) {


	/*def insertStats(s: String, d: GraphLOD): Unit = {

	}
      */

	var driver = com.typesafe.slick.driver.db2.DB2Driver.api

	val url = "jdbc:db2://"+config.dbDb2Host+":"+config.dbDb2Port+"/"+config.dbDb2Database
	val username = config.dbDb2Username
	val password = config.dbDb2Password
	// Class.forName("com.typesafe.slick.driver.db2.DB2Driver")
	Class.forName("com.ibm.db2.jcc.DB2Driver")
	// DriverManager.getConnection(url, username, password)

	val db:Database = Database.forURL(url, username, password, driver="com.ibm.db2.jcc.DB2Driver")
	var connection:Connection = DriverManager.getConnection(url, username, password)


	/**
	 * execute sql and convert async result to sync - http://slick.typesafe.com/doc/3.0.0/sql.html
	 */
	def execute[T](implicit sql: SqlStreamingAction[Vector[T], T, Effect]): Vector[T] = {
		val q = db.run(sql)
		Await.result(q, Duration.Inf)
		val value = q.value.get
		value.get
	}

	def getGroups(s: String): Seq[Group] = {
		val table = s
		var sql = sql"SELECT label, cluster_size FROM #${table}.CLUSTERS WHERE username = 'ontology'".as[(String, Int)]
		var id : Int = -1
		try {
			val result = execute(sql)
			result map tupled((label, cluster_size) => {
				id += 1
				new Group(id, label, cluster_size)
			})
		} catch {
			case e : SqlSyntaxErrorException => println("This dataset has no clusters: " + s)
			Nil
		}
	}


	def getDatasetEntities(name : String) : Int = {
		val sql = sql"SELECT entities FROM PROLOD_MAIN.SCHEMATA WHERE id = ${name}".as[Int]
		execute(sql).headOption.getOrElse(-1)
	}

	def getDatasets(): Seq[Dataset] = {
		var datasets: List[Dataset] = Nil

		val sql = sql"SELECT id, schema_name, entities FROM PROLOD_MAIN.SCHEMATA".as[(String, String, Int)]

		val result = execute(sql) map tupled((id, schema, entities) => {
			new Dataset(id, schema, entities, getGroups(id))
		})
		result.filter(_.size > 0)
	}


	def getStatistics(s: String) : mutable.Map[String, String] = {
		var statistics = mutable.Map[String, String]()
		val statement = connection.createStatement()
		val resultSet = statement.executeQuery("SELECT nodedegreedistribution, averagelinks, edges, gcnodes, connectedcomponents, stronglyconnectedcomponents FROM " + s+ ".graphstatistics")
		while ( resultSet.next() ) {
			statistics += ("nodedegreedistribution" -> resultSet.getString("nodedegreedistribution"))
			statistics += ("averagelinks" -> resultSet.getString("averagelinks"))
			statistics += ("edges" -> resultSet.getString("edges"))
			statistics += ("gcnodes" -> resultSet.getString("gcnodes"))
			statistics += ("connectedcomponents" -> resultSet.getString("connectedcomponents"))
			statistics += ("stronglyconnectedcomponents" -> resultSet.getString("stronglyconnectedcomponents"))
		}
		statistics
	}

	def getColoredPatterns(s: String, id : Int): List[Pattern] = {
		var patterns : List[Pattern] = Nil
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT pattern FROM "+ s+".COLOREDPATTERNS WHERE id = "+id)
			while ( resultSet.next() ) {
				val pattern = resultSet.getString("pattern")
				// val occurences = resultSet.getInt("occurences")

				val patternJsonT = Json.parse(pattern).validate[PatternFromDB]
				val patternsV = List(patternJsonT).filter(p => p.isSuccess).map(p => p.get)
				val errors = List(patternJsonT).filter(p => p.isError)
				if (errors.nonEmpty) {
					println("Could not validate " + errors)
				}

				val patternJson = Json.parse(pattern).validate[PatternFromDB].get
				patterns :::= List(new Pattern(id, "", -1, patternJson.nodes, patternJson.links)) // new Pattern(id, "", occurences, Nil, Nil)
			}
		} catch {
			case e : SqlSyntaxErrorException => println("This dataset has no patterns: " + s)
		}
		patterns
	}

	def getPatterns(s: String): List[Pattern] = {
		var patterns : List[Pattern] = Nil
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT id, pattern, occurences FROM "+ s+".PATTERNS ORDER BY occurences ASC")
			while ( resultSet.next() ) {
				val id = resultSet.getInt("id")
				val pattern = resultSet.getString("pattern")
				val occurences = resultSet.getInt("occurences")
				val patternJson = Json.parse(pattern).validate[PatternFromDB].get
				patterns :::= List(new Pattern(id, "", occurences, patternJson.nodes, patternJson.links)) // new Pattern(id, "", occurences, Nil, Nil)
			}
		} catch {
			case e : SqlSyntaxErrorException => println("This dataset has no patterns: " + s)
		}
		patterns
	}

	def getEntityDetails(dataset: String, entity: String): Entity = {
		var entityDetails = new Entity(entity, entity, Nil)
		entityDetails
	}

	def insert: DBIO[Unit] = DBIO.seq(
		// sqlu"INSERT INTO PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') VALUES ('caterpillar','caterpillar',20,3)"
	)

	def insertDataset(name : String, tuples: Int, entities: Int) {
		try {
			val statement = connection.createStatement()
			val resultSet = statement.execute("INSERT INTO PROLOD_MAIN.SCHEMATA (ID, SCHEMA_NAME, TUPLES, ENTITIES) VALUES ('"+name+"','"+name+"',"+tuples+","+entities+")")
		} catch {
			case e : SqlIntegrityConstraintViolationException => println("Dataset already exists")
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("CREATE SCHEMA " + name)
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("DROP TABLE "+name+".patterns")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}
		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("CREATE TABLE "+name+".patterns (id INT, pattern CLOB, occurences INT)")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("DROP TABLE "+name+".coloredpatterns")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}
		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("CREATE TABLE "+name+".coloredpatterns (id INT, pattern CLOB)")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("DROP TABLE "+name+".graphstatistics")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}
		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("CREATE TABLE "+name+".graphstatistics (nodedegreedistribution CLOB, averagelinks FLOAT, edges INT, connectedcomponents INT, stronglyconnectedcomponents INT, gcnodes INT)")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("DROP TABLE "+name+".CLUSTERS")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("CREATE TABLE "+name+".CLUSTERS "+
				"(                                                                   "+
				"       ID INT NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1),    "+
				"USERNAME VARCHAR(50) DEFAULT 'default' NOT NULL,       "+
				//"SESSION_ID INT NOT NULL,                    "+
				//"SESSION_LOCAL_ID INT,                  "+
				"LABEL VARCHAR(255),                      "+
				//"CHILD_SESSION INT,                         "+
				//"AVG_ERROR FLOAT(53),                         "+
				"CLUSTER_SIZE INT,                              "+
				//"PARTITIONNAME VARCHAR(255) DEFAULT 'MAINTABLE', "+
				"PRIMARY KEY (ID, USERNAME)                                "+
				")")
		} catch {
			case e : SqlSyntaxErrorException => println(e.getMessage)
		}

		try {
			var createStatement = connection.createStatement()

			var createResultSet = createStatement.execute("CREATE TABLE "+name+".MAINTABLE "+
				"("+
				"        SUBJECT_ID INT,                                                       "+
				"        PREDICATE_ID INT,                                                        "+
				"        INTERNALLINK_ID INT,                                                        "+
				"        DATATYPE_ID INT,                                                               "+
				"        NORMALIZEDPATTERN_ID INT,                                                         "+
				"        PATTERN_ID INT,                                                                      "+
				"        PARSED_VALUE FLOAT(53),                                                                 "+
				"        TUPLE_ID INT "+
				"	)");
		}
			/*
					db withSession((session: Session) => {
						(sql"""INSERT INTO PROLOD_MAIN.SCHEMATA (ID, SCHEMA_NAME, TUPLES, ENTITIES) VALUES ('caterpillar','caterpillar',20,3)""")
					}
					*/

		     /*
		db withSession {
			val schemata = TableQuery[Schematas]
			schemata.insertStatement
			var res1: String = "insert into PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') values (?,?,?,?)"
			schemata += Schemata("caterpillar","caterpillar",20,3)
			implicit session => schemata.run
		}
		*/


		/*

  val plainQuery = sql"INSERT INTO PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') VALUES ('caterpillar','caterpillar',20,3)"

  println("Generated SQL for plain query:\n" + plainQuery.getStatement)

  // Execute the query
  println(plainQuery.list)
          */

		/*
db withSession { implicit sess =>
val st = sess.createStatement()
st.execute("INSERT INTO PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') VALUES ('caterpillar','caterpillar',20,3)")

}
       */


		// sql"INSERT INTO PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') VALUES ('caterpillar','caterpillar','20','3')"


		/*
		implicit val getSupplierResult = GetResult(r => r.nextString)
		System.out.println(getSupplierResult)
          */
	}

	def insertPatterns(name: String, patterns: util.HashMap[Integer, util.HashMap[String, Integer]], coloredPatterns: util.HashMap[Integer, util.List[String]]) {
		val coloredPatternsMap = coloredPatterns.asScala.toMap
		val patternsMap = patterns.asScala.toMap
		patternsMap.foreach {
			case (id, patternHashMap) => {
				patternHashMap.asScala.toMap.foreach {
					case (pattern, occurences) => {
						try {
							val statement = connection.createStatement()
							val resultSet = statement.execute("INSERT INTO " + name + ".PATTERNS (ID, PATTERN, OCCURENCES) VALUES (" + id + ", '" + pattern + "'," + occurences + ")")
						} catch {
							case e: SqlIntegrityConstraintViolationException => println("Pattern already exists")
							case e: SqlException => println(e.getMessage)
							case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + "INSERT INTO " + name + ".PATTERNS (ID, PATTERN, OCCURENCES) VALUES (" + id + ", '" + pattern + "'," + occurences + ")")
						}
						val cPattern = coloredPatternsMap.get(id).get.asScala.toList
						cPattern.foreach { case (coloredpattern) =>
							try {
								val statement = connection.createStatement()
								val resultSet = statement.execute("INSERT INTO " + name + ".coloredpatterns (ID, PATTERN) VALUES (" + id + ", '" + coloredpattern + "')")
								// println(pattern)
							} catch {
								case e: SqlException => {
									println(e.getMessage)
									println(coloredpattern)
								}
							}
						}
					}
				}

			}
		}
	}

	def insertStatistics(name: String, nodes: String, links: Double, edges: Int, gcNodes : Int, connectedcomponents : Int, stronglyconnectedcomponents : Int) = {
		try {
			val statement = connection.createStatement()
			val resultSet = statement.execute("INSERT INTO " + name + ".graphstatistics (nodedegreedistribution, averagelinks, edges, gcnodes, connectedcomponents, stronglyconnectedcomponents) VALUES ('" + nodes + "'," + links + ", " + edges + ", " + gcNodes + ", " + connectedcomponents + ", " + stronglyconnectedcomponents + ")")
		} catch {
			case e: SqlIntegrityConstraintViolationException => println(e.getMessage)
			case e: SqlException => println(e.getMessage)
		}
	}

	def insertClasses(name: String, clusters: util.List[String]) = {
		var clusterUris = clusters.asScala.toList
		clusterUris.foreach {
			case (cluster) =>
				try {
					val statement = connection.createStatement()
					val resultSet = statement.execute("INSERT INTO " + name + ".clusters (label, cluster_size, username) VALUES ('" + cluster + "', 0 , 'ontology')")
				} catch {
					case e: SqlIntegrityConstraintViolationException => println(e.getMessage + System.lineSeparator() + "INSERT INTO " + name + ".clusters (label, cluster_size, username) VALUES ('" + cluster + "', 0 , 'ontology')")
					case e: SqlException => println(e.getMessage + System.lineSeparator() + "INSERT INTO " + name + ".clusters (label, cluster_size, username) VALUES ('" + cluster + "', 0 , 'ontology')")
					case e: SqlSyntaxErrorException   => println(e.getMessage + System.lineSeparator() + "INSERT INTO " + name + ".clusters (label, cluster_size, username) VALUES ('" + cluster + "', 0 , 'ontology')")
				}
		}

	}

}
