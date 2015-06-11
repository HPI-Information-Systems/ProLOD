package prolod.common.config

import java.sql.{Connection, DriverManager}

import com.typesafe.slick.driver.db2.DB2Driver
import com.typesafe.slick.driver.db2.DB2Driver.api._
import prolod.common.models.{Group, Dataset}
import slick.jdbc.{GetResult, StaticQuery => Q}
import scala.slick.jdbc.StaticQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.slick.jdbc.JdbcBackend.Session
import scala.util.control.NonFatal

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

	 var db : Database = null

	 var driver = com.typesafe.slick.driver.db2.DB2Driver.api

	 val url = "jdbc:db2://"+config.dbDb2Host+":"+config.dbDb2Port+"/"+config.dbDb2Database
	 var username = config.dbDb2Username
	 var password = config.dbDb2Password
	 Class.forName("com.typesafe.slick.driver.db2.DB2Driver")
	 // Class.forName("com.ibm.db2.jcc.DB2Driver");
	 // DriverManager.getConnection(url, username, password)

	 db = Database.forURL(url, username, password, driver="com.ibm.db2.jcc.DB2Driver")


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

	def getDatasets() : List[Dataset] = {
		var datasets : List[Dataset] = Nil
		var connection:Connection = DriverManager.getConnection(url, username, password)
		val statement = connection.createStatement()
		val resultSet = statement.executeQuery("SELECT id, schema_name, entities FROM PROLOD_MAIN.SCHEMATA")
		while ( resultSet.next() ) {
			val id = resultSet.getString("id")
			val name = resultSet.getString("schema_name")
			val entities = resultSet.getInt("entities")
			if (entities > 0) {
				datasets = datasets ::: List(new Dataset(id, name, entities, Nil))
			}
		}
		datasets
	 }

	def insert: DBIO[Unit] = DBIO.seq(
		// sqlu"INSERT INTO PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') VALUES ('caterpillar','caterpillar',20,3)"
	)

	def insertDataset(name : String) {
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

 }
