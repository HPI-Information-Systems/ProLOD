package prolod.common.config

import java.sql.{Connection, DriverManager}
import java.util
import com.ibm.db2.jcc.am.{SqlIntegrityConstraintViolationException, SqlSyntaxErrorException}
import com.typesafe.slick.driver.db2.DB2Driver
import com.typesafe.slick.driver.db2.DB2Driver.api._
import prolod.common.models.{PatternFromDB, Pattern, Group, Dataset}
import slick.jdbc.{GetResult, StaticQuery => Q}
import scala.slick.jdbc.StaticQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.jdbc.JdbcBackend.Session
import scala.util.control.NonFatal
import scala.collection.JavaConverters._
import play.api.libs.json._
import prolod.common.models.PatternFormats.patternFormat
import prolod.common.models.PatternFormats.patternDBFormat


case class Schemata(id : String, schema_name : String, entities : Int, tuples : Int)

class Schematas(tag: Tag)
	extends Table[Schemata](tag, "PROLOD_MAIN.SCHEMATA") {

	def id = column[String]("id", O.PrimaryKey)
	def schema_name = column[String]("schema_name", O.NotNull)
	def entities = column[Int]("entities", O.NotNull)
	def tuples = column[Int]("tuples", O.NotNull)

	def * = (id, schema_name, entities, tuples) <> (Schemata.tupled, Schemata.unapply)
}

class DatabaseConnection(config : Configuration) {


	/*def insertStats(s: String, d: GraphLOD): Unit = {

	}
      */
	var db : Database = null

	var driver = com.typesafe.slick.driver.db2.DB2Driver.api

	val url = "jdbc:db2://"+config.dbDb2Host+":"+config.dbDb2Port+"/"+config.dbDb2Database
	var username = config.dbDb2Username
	var password = config.dbDb2Password
	// Class.forName("com.typesafe.slick.driver.db2.DB2Driver")
	Class.forName("com.ibm.db2.jcc.DB2Driver");
	// DriverManager.getConnection(url, username, password)

	db = Database.forURL(url, username, password, driver="com.ibm.db2.jcc.DB2Driver")
	var connection:Connection = DriverManager.getConnection(url, username, password)


	def getDB() : Database = {
		db
	}

	def getSuppliers(): DBIO[Seq[String]] =
		sql"SELECT id from PROLOD_MAIN.SCHEMATA".as[String]

	def selectDatasets(implicit session: Session): Unit = {
		/*
		implicit val getResult = GetResult(r => Schemata(r.<<, r.<<, r.<<, r.<<))
		StaticQuery.queryNA[Schemata]("select * from PROLOD_MAIN.SCHEMATA") foreach { c =>
			println("* " + c.id)
		}
		*/
	}

	def getGroups(s: String): List[Group] = {
		var groups : List[Group] = Nil
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT label, cluster_size FROM "+ s+".CLUSTERS WHERE username = 'ontology'")
			var id : Int = 0
			while ( resultSet.next() ) {
				val label = resultSet.getString("label")
				val size = resultSet.getInt("cluster_size")
				groups :::= List(new Group(id, label, size))
				id += 1
			}
		} catch {
			case e : SqlSyntaxErrorException => println("This dataset has no clusters: " + s)
		}
		groups
	}

	def getDatasets() : List[Dataset] = {
		var datasets : List[Dataset] = Nil
		val statement = connection.createStatement()
		val resultSet = statement.executeQuery("SELECT id, schema_name, entities FROM PROLOD_MAIN.SCHEMATA")
		while ( resultSet.next() ) {
			val id = resultSet.getString("id")
			val name = resultSet.getString("schema_name")
			val entities = resultSet.getInt("entities")
			if (entities > 0) {
				datasets :::= List(new Dataset(id, name, entities, getGroups(id)))
			}
		}
		datasets
	 }


	def getPatterns(s: String): List[Pattern] = {
		var patterns : List[Pattern] = Nil
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT id, pattern, occurences FROM "+ s+".PATTERNS")
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
			case e : SqlSyntaxErrorException => println("Schema already exists")
		}

		try {
			var createStatement = connection.createStatement()
			var createResultSet = createStatement.execute("CREATE TABLE "+name+".patterns (id INT not null GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1), pattern CLOB, occurences INT, PRIMARY KEY (id))")
		} catch {
			case e : SqlSyntaxErrorException => println("Table already exists")
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

	def insertPatterns(name: String, patterns: util.HashMap[String, Integer]) {
		patterns.asScala.toMap.foreach { case (pattern, occurences) =>
			try {
				val statement = connection.createStatement()
				val resultSet = statement.execute("INSERT INTO "+name+".PATTERNS (PATTERN, OCCURENCES) VALUES ('" + pattern + "'," + occurences + ")")
			} catch {
				case e: com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException => println("Pattern already exists")
			}
		}

	}


}
