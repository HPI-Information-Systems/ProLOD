package prolod.common.config

import java.io.{File, FileNotFoundException}
import java.sql._
import java.{lang, util}

import com.ibm.db2.jcc.am.{SqlDataException, SqlException, SqlIntegrityConstraintViolationException, SqlSyntaxErrorException}
import com.typesafe.slick.driver.db2.DB2Driver.api._
import de.hpi.fgis.loducc.Keyness
import play.api.libs.json._
import prolod.common.models.PatternFormats.patternDBFormat
import prolod.common.models.{Dataset, Group, Pattern, PatternFromDB, _}
import slick.jdbc.{StaticQuery => Q}
import slick.profile.SqlStreamingAction

import scala.Function._
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source

//import graphlod.dataset._

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

//noinspection RedundantBlock
class DatabaseConnection(config: Configuration) {
	var driver = com.typesafe.slick.driver.db2.DB2Driver.api

	val url = "jdbc:db2://" + config.dbDb2Host + ":" + config.dbDb2Port + "/" + config.dbDb2Database
	val username = config.dbDb2Username
	val password = config.dbDb2Password
	// Class.forName("com.typesafe.slick.driver.db2.DB2Driver")
	Class.forName("com.ibm.db2.jcc.DB2Driver")
	// DriverManager.getConnection(url, username, password)

	val db: Database = Database.forURL(url, username, password, driver = "com.ibm.db2.jcc.DB2Driver")
	var connection: Connection = DriverManager.getConnection(url, username, password)

	/**
		* execute sql and convert async result to sync - http://slick.typesafe.com/doc/3.0.0/sql.html
		*/
	def execute[T](implicit sql: SqlStreamingAction[Vector[T], T, Effect]): Vector[T] = {
		val q = db.run(sql)
		Await.result(q, Duration.Inf)
		val value = q.value.get
		value.get
	}

	def dropMainTables(dataset: String): Unit = {
		val sqlDir = new File("prolod-preprocessing/sql/")
		for (file <- sqlDir.listFiles) {
			try {
				val queryString = Source.fromFile(file.getPath).mkString
				val query = String.format(queryString, dataset)
				try {
					getTableNameFromStatement(queryString) match {
						case tableName => {
							try {
								var dropStatement = connection.createStatement()
								var tableNamenormalized = tableName
								if (tableName.equals("")) tableNamenormalized = file.getName.replace(".sql", "")
								dropStatement.execute("DROP TABLE " + dataset + "." + tableNamenormalized)
								dropStatement.close()
							} catch {
								case e: SqlSyntaxErrorException => println(e.getMessage)
							}
						}
					}
				} catch {
					case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + query)
				}
			} catch {
				case e: SqlSyntaxErrorException => println(e.getMessage)
				case e: FileNotFoundException => println(e.getMessage)
			}
		}
	}

	def dropTables(dataset: String): Unit = {
		dropTable(dataset + ".patterns")
		dropTable(dataset + ".coloredpatterns")
		dropTable(dataset + ".coloredisopatterns")
		dropTable(dataset + ".patterns_gc")
		dropTable(dataset + ".coloredpatterns_gc")
		dropTable(dataset + ".coloredisopatterns_gc")
		dropTable(dataset + ".patterns_bc")
		dropTable(dataset + ".coloredpatterns_bc")
		dropTable(dataset + ".coloredisopatterns_bc")
		dropTable(dataset + ".graphstatistics")
		dropTable(dataset + ".CLUSTERS")
		dropTable(dataset + ".cluster_hierarchy")
	}

	private def dropTable(name: String): Unit = {
		try {
			val createStatement = connection.createStatement()
			createStatement.execute("DROP TABLE " + name)
			createStatement.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
	}

	def createTables(name: String): Unit = {
		try {
			val createStatement = connection.createStatement()
			val createResultSet = createStatement.execute("CREATE SCHEMA " + name)
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}

		createTable(name + ".patterns (id INT, name VARCHAR(200), pattern CLOB, occurences INT, diameter FLOAT, nodedegreedistribution CLOB)")
		createTable(name + ".coloredpatterns (id INT, pattern CLOB)")
		createTable(name + ".coloredisopatterns (id INT, pattern_id INT, pattern CLOB)")
		createTable(name + ".PATTERNS_GC (id INT, name VARCHAR(200), pattern CLOB, occurences INT, diameter FLOAT, nodedegreedistribution CLOB)")

		createTable(name + ".COLOREDPATTERNS_GC (id INT, pattern CLOB)")
		createTable(name + ".coloredisopatterns_GC (id INT, pattern_id INT, pattern CLOB)")
		createTable(name + ".graphstatistics (nodedegreedistribution CLOB, averagelinks FLOAT, edges INT, connectedcomponents INT, stronglyconnectedcomponents INT, gcnodes INT, gcedges INT, highestIndegrees CLOB, highestOutdegrees CLOB)")
		createTable(name + ".CLUSTERS " +
			"(                                                                   " +
			"       ID INT NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1),    " +
			"USERNAME VARCHAR(50) DEFAULT 'default' NOT NULL,       " +
			//"SESSION_ID INT NOT NULL,                    "+
			//"SESSION_LOCAL_ID INT,                  "+
			"LABEL VARCHAR(255),                      " +
			//"CHILD_SESSION INT,                         "+
			//"AVG_ERROR FLOAT(53),                         "+
			"CLUSTER_SIZE INT,                              " +
			//"PARTITIONNAME VARCHAR(255) DEFAULT 'MAINTABLE', "+
			"PRIMARY KEY (ID, USERNAME)                                " +
			")")

		createTable(name + ".cluster_hierarchy (cluster VARCHAR(255), subcluster VARCHAR(255))")

		val sqlDir = new File("prolod-preprocessing/sql/")
		for (file <- sqlDir.listFiles) {
			try {
				val queryString = Source.fromFile(file.getPath).mkString
				val query = String.format(queryString, name)
				try {
					val statement = connection.prepareStatement(query)
					statement.execute
					statement.close()
				} catch {
					case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + query)
				}
			} catch {
				case e: SqlSyntaxErrorException => println(e.getMessage)
				case e: FileNotFoundException => println(e.getMessage)
			}
		}
	}

	def createTable(tableDefinition: String): Unit = {
		try {
			val createStatement = connection.createStatement()
			createStatement.execute("CREATE TABLE " + tableDefinition)
			createStatement.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
	}

	def createIndices(name: String) = {
		val sqlDir = new File("prolod-preprocessing/sql/indices/")
		try {
			for (file <- sqlDir.listFiles) {
				try {
					val queryString = Source.fromFile(file.getPath).mkString
					val r = "/\\*[\\s\\S]*?\\*/|--[^\\r\\n]*|;"
					val queries = queryString.split(r)
					for (queryString <- queries) {
						val query = String.format(queryString, name)
						try {
							val statement = connection.prepareStatement(query)
							statement.execute
							statement.close()
						} catch {
							case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + query)
						}
					}
				} catch {
					case e: SqlSyntaxErrorException => println(e.getMessage)
					case e: FileNotFoundException => println(e.getMessage)
				}
			}
		} catch {
			case e: NullPointerException => println(e.getMessage)
		}
	}

	def getTableNameFromStatement(s: String): String = {
		val pattern = """%s.(.*)""".r

		s match {
			case pattern(group) => {
				group
			}
			case _ => {
				""
			}
		}
		/*
		var result = pattern.findFirstIn(s).gro//for (m <- pattern findFirstIn s) yield m
		println(s + result)
		result                */
	}

	def validateDatasetString(table: String) = {
		if (!table.matches("[A-Za-z]+")) {
			//throw new RuntimeException("illegal table name: " + table)
		}
	}

	def getClusters(dataset: String, ontologyNamespace: String): Seq[Group] = {
		validateDatasetString(dataset)
		val sql = sql"SELECT id, label, cluster_size FROM #${dataset}.CLUSTERS WHERE username = 'ontology' ORDER BY LABEL".as[(Int, String, Int)]
		try {
			val result = execute(sql)
			result map tupled((id, label, cluster_size) => {
				if (!ontologyNamespace.equals("")) {
					new Group(id, removeOntologyNamespace(label, ontologyNamespace), cluster_size)
				} else {
					new Group(id, label, cluster_size)
				}

			})
		} catch {
			case e: SqlSyntaxErrorException => println("This dataset has no clusters: " + dataset + e.getMessage + e.getLocalizedMessage)
				Nil
		}
	}

	def getClusterNames(dataset: String, ontologyNamespace: String): Seq[String] = {
		validateDatasetString(dataset)
		val sql = sql"SELECT label FROM #${dataset}.CLUSTERS WHERE username = 'ontology' ORDER BY LABEL".as[(String)]
		try {
			val result = execute(sql)
			result map ((label) => {
				if (!ontologyNamespace.equals("")) {
					removeOntologyNamespace(label, ontologyNamespace)
				} else {
					label
				}

			})
		} catch {
			case e: SqlSyntaxErrorException => println("This dataset has no clusters: " + dataset + e.getMessage + e.getLocalizedMessage)
				Nil
		}
	}

	private def removeOntologyNamespace(name: String, ontologyNamespace: String): String = {
		var result = name
		if (ontologyNamespace != null) {
			if (ontologyNamespace.startsWith("\"group\":\"")) {
				result = result.replace(ontologyNamespace, "\"group\":\"")
			} else {
				result = result.replace(ontologyNamespace, "")
			}
		}
		result
	}

	def getDatasetEntities(name: String): Int = {
		val sql = sql"SELECT entities FROM PROLOD_MAIN.SCHEMATA WHERE id = ${name}".as[Int]
		execute(sql).headOption.getOrElse(-1)
	}

	def getDatasets(): Seq[Dataset] = {
		try {
			val sql = sql"SELECT id, schema_name, entities, ontology_namespace FROM PROLOD_MAIN.SCHEMATA ORDER BY LOWER(schema_name)".as[(String, String, Int, String)]

			val result = execute(sql) map tupled((id, schema, entities, ontology_namespace) => {
				new Dataset(id, schema, entities, getClusters(id, ontology_namespace))
			})
			result.filter(_.size > 0)
		} catch {
			case e: SqlSyntaxErrorException => {
				val sql = sql"SELECT id, schema_name, entities FROM PROLOD_MAIN.SCHEMATA ORDER BY LOWER(schema_name)".as[(String, String, Int)]
				val result = execute(sql) map tupled((id, schema, entities) => {
					new Dataset(id, schema, entities, getClusters(id, ""))
				})
				result.filter(_.size > 0)
			}
		}
	}

	def insertKeyness(dataset: String, keyness: Keyness) = {
		dropTable(dataset + ".keyness")
		try {
			val createStatement = connection.createStatement()
			createStatement.execute("CREATE TABLE " + dataset + ".keyness (cluster_id INT, property_id INT, keyness FLOAT, uniqueness FLOAT, density FLOAT, values INT)")
			createStatement.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
		val clusters: Seq[Group] = getClusters(dataset, "")
		for (cluster: Group <- clusters) {
			val triples: util.HashMap[Integer, util.HashMap[Integer, Integer]] = new util.HashMap()
			val clusterId = cluster.id
			val sql = sql"SELECT subject_id FROM #$dataset.Cluster_subjects WHERE cluster_id = #$clusterId".as[(Int)]
			val result = execute(sql) map ((subject_id) => {
				var propertyValuePairs: util.HashMap[Integer, Integer] = new util.HashMap()
				val sql2 = sql"SELECT predicate_id, tuple_id FROM #$dataset.maintable WHERE subject_id = #$subject_id".as[(Int, Int)]
				val result = execute(sql2) map tupled((predicate_id, tuple_id) => {
					propertyValuePairs.put(predicate_id.asInstanceOf[Integer], tuple_id.asInstanceOf[Integer])
				})
				triples.put(subject_id.asInstanceOf[Integer], propertyValuePairs)
			})
			val keynessStats: util.HashMap[Integer, util.HashMap[String, lang.Double]] = keyness.getKeyness("", triples)
			keynessStats.asScala.toMap.foreach {
				case (property, udk) => {
					try {
						val statement = connection.createStatement()
						val resultSet = statement.execute("INSERT INTO " + dataset + ".keyness (cluster_id, property_id, keyness, uniqueness, density, values) VALUES (" + clusterId + "," + property + "," + udk.get("keyness") + "," + udk.get("uniqueness") + "," + udk.get("density") + "," + udk.get("properties").toInt + ")")
					} catch {
						case e: SqlIntegrityConstraintViolationException => println("Dataset already exists")
					}
				}
			}
		}
	}

	def getClusterName(dataset: String, i: Int): String = {
		val sql = sql"""SELECT label FROM #${dataset}.CLUSTERS WHERE username = 'ontology' AND ID = '#${i}'""".as[(String)]
		val clusterUri = execute(sql).head
		val ontNs = getOntologyNamespace(dataset)
		clusterUri.replace(ontNs, "")
	}

	def getClusterSize(dataset: String, i: Int): Int = {
		val sql = sql"""SELECT cluster_size FROM #${dataset}.CLUSTERS WHERE username = 'ontology' AND ID = '#${i}'""".as[(Int)]
		execute(sql).head
	}

	def getKeyness(dataset: String, groups: List[String]): Seq[KeynessResult] = {
		validateDatasetString(dataset)
		var keynessList: Seq[KeynessResult] = Nil

		if (groups.isEmpty) {
			/*
			val sqlProperties = sql"select property_id, keyness, uniqueness, density, values FROM #$dataset.keyness".as[(Int, Double, Double, Double, Int)]
			val result = execute(sqlProperties)
			keynessList = result map tupled((property_id, keyness, uniqueness, density, values) => {
				new KeynessResult(getProperty(dataset, property_id), keyness, uniqueness, density, values, dataset)
			})
							*/


			val sqlProperties = sql"SELECT property_id, keyness, uniqueness, density, values, cluster_id FROM #$dataset.keyness ORDER BY property_id".as[(Int, Double, Double, Double, Int, Int)]
			val result = execute(sqlProperties)
			// TODO filter groups
			var propertyId: Int = -1
			var tempKeynessList: Seq[Double] = Nil
			var tempUniquenessList: Seq[Double] = Nil
			var tempDensityList: Seq[Double] = Nil
			var clusterSizes: Seq[Int] = Nil
			result foreach (k => {
				var clusterId: Int = k._6
				if ((propertyId > -1) && (k._1 != propertyId)) {
					keynessList = keynessList :+ getKeynessResultsForClusters(dataset, propertyId, clusterSizes, tempKeynessList, tempUniquenessList, tempDensityList)
					tempKeynessList = Nil
					tempUniquenessList = Nil
					tempDensityList = Nil
					clusterSizes = Nil
				}
				tempKeynessList = tempKeynessList :+ k._2
				tempUniquenessList = tempUniquenessList :+ k._3
				tempDensityList = tempDensityList :+ k._4
				clusterSizes = clusterSizes :+ k._5
				propertyId = k._1
			})
			keynessList = keynessList :+ getKeynessResultsForClusters(dataset, propertyId, clusterSizes, tempKeynessList, tempUniquenessList, tempDensityList)
			/*
						keynessList = result map tupled((property_id, keyness, uniqueness, density, cluster_id) => {
							if ((propertyId > -1) && (property_id != propertyId)) {

								new KeynessResult(getProperty(dataset, property_id), keyness, uniqueness, density, getKeynessValues(dataset, property_id, cluster_id), getClusterName(dataset, cluster_id))
							}
							propertyId = property_id
						})*/
		} else {
			val sqlProperties = sql"SELECT property_id, keyness, uniqueness, density, cluster_id FROM #$dataset.keyness ORDER BY keyness".as[(Int, Double, Double, Double, Int)]
			val result = execute(sqlProperties)
			// TODO filter groups
			keynessList = result map tupled((property_id, keyness, uniqueness, density, cluster_id) => {
				new KeynessResult(getProperty(dataset, property_id), keyness, uniqueness, density, getKeynessValues(dataset, property_id, cluster_id), getClusterName(dataset, cluster_id))
			}) filter (e => groups.contains(e.cluster))
		}
		keynessList
	}

	def getKeynessResultsForClusters(dataset: String, propertyId: Int, clusterSizes: Seq[Int], tempKeynessList: Seq[Double], tempUniquenessList: Seq[Double], tempDensityList: Seq[Double]): KeynessResult = {
		val clusterEntitySum: Int = clusterSizes.sum
		var keyness: Double = 0
		var uniqueness: Double = 0
		var density: Double = 0
		var i: Int = 0
		clusterSizes foreach (clusterSize => {
			keyness = keyness + clusterSize.toFloat / clusterEntitySum * tempKeynessList(i)
			uniqueness = uniqueness + clusterSize.toFloat / clusterEntitySum * tempUniquenessList(i)
			density = density + (clusterSize.toFloat / clusterEntitySum * tempDensityList(i))
			println(clusterSize + " " + clusterEntitySum)
			i += 1
		})
		new KeynessResult(getProperty(dataset, propertyId), keyness, uniqueness, density, clusterEntitySum, dataset)
	}

	def getKeynessValues(dataset: String, i: Int, i1: Int): Int = {
		val sqlProperties = sql"SELECT values FROM #$dataset.keyness WHERE property_id = #$i AND cluster_id = #$i1".as[(Int)]
		execute(sqlProperties).head
	}

	def getStatistics(dataset: String): mutable.Map[String, String] = {
		validateDatasetString(dataset)
		val statistics = mutable.Map[String, String]()
		val sql = sql"SELECT nodedegreedistribution, averagelinks, edges, connectedcomponents, stronglyconnectedcomponents FROM #$dataset.graphstatistics".as[(String, Float, Int, Int, Int)]
		try {
			val result = execute(sql) map tupled((nodedegreedistribution, averagelinks, edges, connectedcomponents, stronglyconnectedcomponents) => {
				statistics += ("nodedegreedistribution" -> nodedegreedistribution)
				statistics += ("averagelinks" -> averagelinks.toString)
				statistics += ("edges" -> edges.toString)
				statistics += ("connectedcomponents" -> connectedcomponents.toString)
				statistics += ("stronglyconnectedcomponents" -> stronglyconnectedcomponents.toString)
			})
			val sql2 = sql"SELECT gcnodes, highestIndegrees, highestOutdegrees FROM #$dataset.graphstatistics".as[(Int, String, String)]
			val result2 = execute(sql2) map tupled((gcnodes, highestIndegrees, highestOutdegrees) => {
				statistics += ("gcnodes" -> gcnodes.toString)
				statistics += ("highestIndegrees" -> highestIndegrees)
				statistics += ("highestOutdegrees" -> highestOutdegrees)
			})
			val sql3 = sql"SELECT gcedges FROM #$dataset.graphstatistics".as[Int]
			val result3 = execute(sql3) map ((gcedges) => {
				statistics += ("gcedges" -> gcedges.toString)
			})
		} catch {
			case e: SqlSyntaxErrorException => {
				val sql2 = sql"SELECT gcnodes FROM #$dataset.graphstatistics".as[(Int)]
				val result2 = execute(sql2) map ((gcnodes) => {
					statistics += ("gcnodes" -> gcnodes.toString)
				})
				println("error getting stats: " + e.getMessage + System.lineSeparator() + sql.toString)
			}
		}
		statistics
	}

	def getColoredPatterns(dataset: String, id: Int, gc: Option[String]): List[Pattern] = {
		val dbExt = gc.getOrElse("")
		var patterns: List[Pattern] = Nil
		val sql = sql"SELECT ontology_namespace FROM PROLOD_MAIN.SCHEMATA WHERE ID = ${dataset}".as[String]
		val namespaces: Vector[String] = execute(sql) map (ontology_namespace => {
			"\"group\":\"" + ontology_namespace.replace("/", "\\/")
		})
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT pattern FROM " + dataset + ".COLOREDPATTERNS" + dbExt + " WHERE id = " + id)
			while (resultSet.next()) {
				var pattern = resultSet.getString("pattern")
				namespaces foreach (ontology_namespace => {
					pattern = removeOntologyNamespace(pattern, ontology_namespace)
				})
				// val occurences = resultSet.getInt("occurences")
				val patternJsonT = Json.parse(pattern).validate[PatternFromDB]
				val patternsV = List(patternJsonT).filter(p => p.isSuccess).map(p => p.get)
				val errors = List(patternJsonT).filter(p => p.isError)
				if (errors.nonEmpty) {
					println("Could not validate " + errors)
				}
				val patternJson = Json.parse(pattern).validate[PatternFromDB].get
				var isoGroup = -1
				try {
					val sqlIso = sql"""SELECT pattern_id FROM #${dataset}.COLOREDISOPATTERNS#${dbExt} WHERE ID = #${id}""".as[Int]
					val resultIso = execute(sqlIso)
					resultIso.foreach((isoId) => {
						isoGroup = isoId
					})
				} catch {
					case e: SqlSyntaxErrorException => println(e.getMessage)
				}
				patterns :::= List(new Pattern(id, "", -1, patternJson.nodes, patternJson.links, -1, Some(isoGroup))) // new Pattern(id, "", occurences, Nil, Nil)
			}
		} catch {
			case e: SqlSyntaxErrorException => println("This dataset has no colored patterns: " + dataset)
		}
		patterns
	}

	def getColoredIsoPatterns(dataset: String, id: Int, gc: Option[String]): List[Pattern] = {
		val dbExt = gc.getOrElse("")
		var patterns: List[Pattern] = Nil
		val sql = sql"SELECT ontology_namespace FROM PROLOD_MAIN.SCHEMATA WHERE ID = ${dataset}".as[String]
		val namespaces: Vector[String] = execute(sql) map (ontology_namespace => {
			"\"group\":\"" + ontology_namespace.replace("/", "\\/")
		})
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT id, pattern FROM " + dataset + ".COLOREDISOPATTERNS" + dbExt + " WHERE pattern_id = " + id)
			while (resultSet.next()) {
				var pattern = resultSet.getString("pattern")
				var isoId = resultSet.getInt("id")
				namespaces foreach (ontology_namespace => {
					pattern = removeOntologyNamespace(pattern, ontology_namespace)
				})
				var occurences: Int = 0
				val sqlOcc = sql"""SELECT COUNT(*) FROM #${dataset}.COLOREDPATTERNS#${dbExt} WHERE ID = #${isoId}""".as[(Int)]
				val resultOcc = execute(sqlOcc)
				resultOcc.foreach((occurencesP) => {
					occurences = occurencesP
				})
				// val occurences = resultSet.getInt("occurences")
				val patternJsonT = Json.parse(pattern).validate[PatternFromDB]
				val patternsV = List(patternJsonT).filter(p => p.isSuccess).map(p => p.get)
				val errors = List(patternJsonT).filter(p => p.isError)
				if (errors.nonEmpty) {
					println("Could not validate " + errors)
				}
				val patternJson = Json.parse(pattern).validate[PatternFromDB].get
				patterns :::= List(new Pattern(isoId, "", occurences, patternJson.nodes, patternJson.links)) // new Pattern(id, "", occurences, Nil, Nil)
			}
		} catch {
			case e: SqlSyntaxErrorException => println("This dataset has no patterns: " + dataset)
		}
		patterns.sortWith(_.occurences > _.occurences)
	}

	def getPatterns(s: String, gc: Option[String]): List[Pattern] = {
		var dbExt = gc.getOrElse("")
		var patterns: List[Pattern] = Nil
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT id, name, pattern, occurences FROM " + s + ".PATTERNS" + dbExt + " ORDER BY occurences ASC")
			while (resultSet.next()) {
				val id = resultSet.getInt("id")
				val pattern = resultSet.getString("pattern")
				val occurences = resultSet.getInt("occurences")
				val name = resultSet.getString("name")
				//val diameter = resultSet.getDouble("diameter")
				val patternJson = Json.parse(pattern).validate[PatternFromDB].get
				patterns :::= List(new Pattern(id, name, occurences, patternJson.nodes, patternJson.links)) // new Pattern(id, "", occurences, Nil, Nil)
			}
		} catch {
			case e: SqlSyntaxErrorException => {
				println("This dataset has no patterns: " + s)
				try {
					val statement = connection.createStatement()
					val resultSet = statement.executeQuery("SELECT id, pattern, occurences FROM " + s + ".PATTERNS" + dbExt + " ORDER BY occurences ASC")
					while (resultSet.next()) {
						val id = resultSet.getInt("id")
						val pattern = resultSet.getString("pattern")
						val occurences = resultSet.getInt("occurences")
						val patternJson = Json.parse(pattern).validate[PatternFromDB].get
						patterns :::= List(new Pattern(id, "", occurences, patternJson.nodes, patternJson.links)) // new Pattern(id, "", occurences, Nil, Nil)
					}
				} catch {
					case e: SqlSyntaxErrorException => {
						println("This dataset has no patterns: " + s)
						if (dbExt.equals("_gc")) {
							dbExt = "_bc"
							try {
								val statement = connection.createStatement()
								val resultSet = statement.executeQuery("SELECT name, pattern FROM " + s + ".PATTERNS" + dbExt)
								var id = 0
								while (resultSet.next()) {
									id += 1
									val pattern = resultSet.getString("pattern")
									val name = resultSet.getString("name")
									val patternJson = Json.parse(pattern).validate[PatternFromDB].get
									patterns :::= List(new Pattern(id, name, -1, patternJson.nodes, patternJson.links)) // new Pattern(id, "", occurences, Nil, Nil)
								}
							} catch {
								case e: SqlSyntaxErrorException => println("This dataset has no patterns: " + s)
							}
						}
					}
				}

			}
		}
		patterns
	}

	def getPatternDiameter(dataset: String, patternId: Int): Int = {
		validateDatasetString(dataset)
		var diameter: Int = 0
		try {
			val sql = sql"SELECT diameter FROM #${dataset}.patterns WHERE ID = ${patternId}".as[(Int)]
			val result = execute(sql)
			result foreach ((diameterSql) => {
				diameter = diameterSql
			})
		} catch {
			case e: SqlSyntaxErrorException => println("error getting diameter" + e.getMessage)
		}
		diameter
	}

	/*
	def getProperties(dataset: String, clusters: Seq[Group]) : String = {

		val sql = sql"""SELECT PREDICATE FROM #${dataset}.PREDICATETABLE WHERE id = #${propertyId}""".as[(String)]
		execute(sql).head
	}
	*/

	def getProperty(dataset: String, propertyId: Int): String = {
		val sql = sql"""SELECT PREDICATE FROM #${dataset}.PREDICATETABLE WHERE ID = #${propertyId}""".as[(String)]
		execute(sql).head
	}

	def getPropertyId(name: String, s: String): Int = {
		validateDatasetString(name)
		var result: Int = -1
		val statement = connection.createStatement()
		val resultSet = statement.executeQuery("SELECT id FROM " + name + ".predicatetable WHERE predicate='" + s + "'")
		resultSet.next()
		result = resultSet.getInt("id")
		statement.close()
		result
	}

	def getPropertyStatistics(dataset: String, clusters: List[String]): Seq[Property] = {
		// TODO filter by cluster
		try {
			val sqlP = sql"SELECT SUM(cnt) FROM #$dataset.predicatetable".as[(Int)]
			val properties = execute(sqlP).head

			val sql = sql"SELECT id, predicate, cnt FROM #$dataset.predicatetable ORDER BY cnt".as[(Int, String, Int)]

			execute(sql) map tupled((id, predicate, cnt) => {
				new Property(id, predicate, cnt, cnt.toFloat / properties)
			})
		} catch {
			case e: SqlSyntaxErrorException => {
				println(e.getMessage)
				Nil
			}
		}
	}

	def getEntityDetails(dataset: String, subjectUri: String): Entity = {
		validateDatasetString(dataset)
		var triples: List[Triple] = Nil
		var label: String = ""
		var subjectId: Int = -1
		var predicateUri = ""
		var objectUri = ""
		try {
			val statement1 = connection.createStatement()
			val resultSet1 = statement1.executeQuery("SELECT id FROM " + dataset + ".subjecttable WHERE subject = '" + subjectUri + "'")
			while (resultSet1.next()) {
				subjectId = resultSet1.getInt("id")
			}
			statement1.close()

			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT tuple_id, predicate_id FROM " + dataset + ".maintable WHERE subject_id = " + subjectId)
			while (resultSet.next()) {
				val objectId = resultSet.getString("tuple_id")
				val predicateId = resultSet.getString("predicate_id")


				val statement2 = connection.createStatement()
				val resultSet2 = statement2.executeQuery("SELECT predicate FROM " + dataset + ".predicatetable WHERE id = " + predicateId)
				while (resultSet2.next()) {
					predicateUri = resultSet2.getString("predicate")
				}
				statement2.close()

				val statement3 = connection.createStatement()
				val resultSet3 = statement3.executeQuery("SELECT object FROM " + dataset + ".objecttable WHERE tuple_id = " + objectId)
				while (resultSet3.next()) {
					objectUri = resultSet3.getString("object")
					if (predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
						label = objectUri
					}
				}
				statement3.close()

				triples :::= List(new Triple(subjectUri, predicateUri, objectUri))
			}
			statement.close()
		} catch {
			case e: SqlSyntaxErrorException => println("error getting entity details" + e.getMessage)
		}

		val entityDetails = new Entity(subjectId, subjectUri, label, triples)
		entityDetails
	}

	def getEntityDetails(dataset: String, subjectId: Int): Entity = {
		validateDatasetString(dataset)
		var triples: List[Triple] = Nil
		var label: String = ""
		var subjectUri = ""
		var predicateUri = ""
		var objectUri = ""
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT tuple_id, predicate_id FROM " + dataset + ".maintable WHERE subject_id = " + subjectId)
			while (resultSet.next()) {
				val objectId = resultSet.getString("tuple_id")
				val predicateId = resultSet.getString("predicate_id")

				val statement1 = connection.createStatement()
				val resultSet1 = statement1.executeQuery("SELECT subject FROM " + dataset + ".subjecttable WHERE id = " + subjectId)
				while (resultSet1.next()) {
					subjectUri = resultSet1.getString("subject")
				}
				statement1.close()

				val statement2 = connection.createStatement()
				val resultSet2 = statement2.executeQuery("SELECT predicate FROM " + dataset + ".predicatetable WHERE id = " + predicateId)
				while (resultSet2.next()) {
					predicateUri = resultSet2.getString("predicate")
				}
				statement2.close()

				val statement3 = connection.createStatement()
				val resultSet3 = statement3.executeQuery("SELECT object FROM " + dataset + ".objecttable WHERE tuple_id = " + objectId)
				while (resultSet3.next()) {
					objectUri = resultSet3.getString("object")
					if (predicateUri.equals("http://www.w3.org/2000/01/rdf-schema#label")) {
						label = objectUri
					}
				}
				statement3.close()

				triples :::= List(new Triple(subjectUri, predicateUri, objectUri))
			}
			statement.close()
		} catch {
			case e: SqlSyntaxErrorException => println("error getting entity details" + e.getMessage)
		}

		val entityDetails = new Entity(subjectId, subjectUri, label, triples)
		entityDetails
	}

	def insert: DBIO[Unit] = DBIO.seq(
		// sqlu"INSERT INTO PROLOD_MAIN.SCHEMATA ('ID', 'SCHEMA_NAME', 'TUPLES', 'ENTITIES') VALUES ('caterpillar','caterpillar',20,3)"
	)

	def replaceDatasetName(query: String, name: String) = {
		query.replace("##dataset##", name)
	}

	def insertDataset(name: String, tuples: Int, entities: Int, ontologyNamespace: String, namespace: String) {
		var entityCount = entities
		try {
			val sql = sql"""SELECT COUNT(*) FROM #${name}.subjecttable""".as[(Int)]
			val result = execute(sql)
			result.foreach((datasetSize) => {
				entityCount = datasetSize
			})
			val statement = connection.createStatement()
			val resultSet = statement.execute("INSERT INTO PROLOD_MAIN.SCHEMATA (ID, SCHEMA_NAME, TUPLES, ENTITIES, ONTOLOGY_NAMESPACE, NAMESPACE) VALUES ('" + name + "','" + name + "'," + tuples + "," + entityCount + ",'" + ontologyNamespace + "','" + namespace + "')")
		} catch {
			case e: SqlIntegrityConstraintViolationException => {
				println("Dataset already exists")
				val statement = connection.createStatement()
				val resultSet = statement.execute("UPDATE PROLOD_MAIN.SCHEMATA SET ENTITIES = " + entityCount + " WHERE SCHEMA_NAME = '" + name + "'")
			}
		}
	}

	def insertPatterns(name: String, patterns: util.HashMap[Integer, util.HashMap[String, Integer]], coloredPatterns: util.HashMap[Integer, util.List[String]], coloredIsoPatterns: util.HashMap[Integer, util.List[String]], diameter: util.HashMap[Integer, lang.Double], subjects: Map[String, Int], gc: Option[String]) {
		validateDatasetString(name)
		val dbExt = gc.getOrElse("")
		val coloredPatternsMap = coloredPatterns.asScala.toMap
		val coloredIsoPatternsMap = coloredIsoPatterns.asScala.toMap
		val diameterMap = diameter.asScala.toMap
		val patternsMap = patterns.asScala.toMap
		var isoCounter: Int = 0
		patternsMap.foreach {
			case (id, patternHashMap) => {
				val patternHashMapScala = patternHashMap.asScala.toMap
				patternHashMapScala.foreach {
					case (pattern, occurences) => {
						val patternDiameter = diameterMap.get(id).getOrElse(-1)
						try {
							val statement = connection.createStatement()
							val patternJson: PatternFromDB = Json.parse(pattern).validate[PatternFromDB].get
							val patternName = patternJson.name.getOrElse("")
							val resultSet = statement.execute("INSERT INTO " + name + ".PATTERNS" + dbExt + " (ID, NAME, PATTERN, OCCURENCES, DIAMETER) VALUES (" + id + ", '" + patternName + "',  '" + pattern + "'," + occurences + "," + patternDiameter + ")")
						} catch {
							case e: SqlIntegrityConstraintViolationException => println("Pattern already exists")
							case e: SqlException => println("error inserting pattern: " + e.getMessage)
							case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + "INSERT INTO " + name + ".PATTERNS" + dbExt + " (ID, PATTERN, OCCURENCES, DIAMETER) VALUES (" + id + ", '" + pattern + "'," + occurences + ", " + patternDiameter + ")")
						}
						val cIsoPattern = coloredIsoPatternsMap.get(id).get.asScala.toList
						cIsoPattern.foreach { case (coloredisopattern) =>
							try {
								val statement = connection.createStatement()
								val resultSet = statement.execute("INSERT INTO " + name + ".coloredisopatterns" + dbExt + " (ID, pattern_id, PATTERN) VALUES (" + isoCounter + ", " + id + ", '" + coloredisopattern + "')")

								val cPattern = coloredPatternsMap.get(isoCounter).get.asScala.toList
								cPattern.foreach { case (coloredpattern) =>
									try {
										// add subject id (db) for patterns
										val patternJson = Json.parse(coloredpattern).validate[PatternFromDB].get
										var nodeList: List[Node] = Nil
										var linkList: List[Link] = Nil
										var nodeMap: Map[Int, Int] = Map()
										for (node <- patternJson.nodes) {
											var newDbId = -1
											if (subjects.contains(node.uri.get)) {
												newDbId = subjects.get(node.uri.get).get
											}
											val newNode = node.copy(dbId = Some(newDbId))
											nodeMap += (node.id -> newNode.id)
											nodeList :::= List(newNode)
										}
										for (link <- patternJson.links) {
											val newLink = link.copy()
											linkList :::= List(newLink)
										}
										val pattern2insert = new PatternFromDB(patternJson.name, nodeList, linkList)
										val newPattern = Json.toJson(pattern2insert).toString()
										val statement = connection.createStatement()
										val resultSet = statement.execute("INSERT INTO " + name + ".coloredpatterns" + dbExt + " (ID, PATTERN) VALUES (" + isoCounter + ", '" + newPattern + "')")
										statement.close()
									} catch {
										case e: SqlException => {
											println("error inserting pattern (" + coloredpattern + ") " + e.getMessage)
										}
										case e: SqlSyntaxErrorException => println(e.getMessage)
									}
								}

								isoCounter += 1
							} catch {
								case e: SqlException => {
									println("error inserting pattern (2)" + e.getMessage)
									println(coloredisopattern)
								}
								case e: SqlSyntaxErrorException => println(e.getMessage)
							}
						}
					}
				}
			}
		}
	}

	def insertPatternsGC(name: String, patterns: util.HashMap[Integer, util.HashMap[String, Integer]], coloredPatterns: util.HashMap[Integer, util.List[String]], coloredIsoPatterns: util.HashMap[Integer, util.List[String]], diameter: util.HashMap[Integer, lang.Double], subjects: Map[String, Int]): Unit = {
		insertPatterns(name, patterns, coloredPatterns, coloredIsoPatterns, diameter, subjects, Some("_gc"))
	}

	def performInsert(table: String, names: Seq[Any], values: Seq[Any]): Option[Int] = {
		validateDatasetString(table)
		val query = String.format("insert into %s (%s) values (%s)",
			table,
			commaize(names.map(n => n.toString).toList),
			commaize(values.map(v => "'" + v.toString.replace("'", "") + "'").toList)
		)
		try {
			val statement = connection.prepareStatement(query)
			statement.execute
			val key = statement.getGeneratedKeys
			statement.close()
			if (key.next) {
				key.getInt(1)
			}
		} catch {
			case e: SqlIntegrityConstraintViolationException => println(e.getMessage + System.lineSeparator() + query)
			case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + query)
			case e: SqlDataException => println(e.getMessage + System.lineSeparator() + query)
			case e: SqlException => println(e.getMessage + System.lineSeparator() + query)
		}
		None
	}

	private def commaize(list: List[_ <: Any]): String = list match {
		case List() => ""
		case List(x) => x.toString
		case _ => list.head + ", " + commaize(list.tail)
	}

	def insertSubject(name: String, s: String): Int = {
		performInsert(name + ".subjecttable", List("subject"), List(s)) match {
			case Some(i) => i
			case None => getSubjectId(name, s)
		}
	}

	def getSubjectId(dataset: String, s: String): Int = {
		validateDatasetString(dataset)
		var result: Int = -1
		val statement = connection.createStatement()
		try {
			val resultSet = statement.executeQuery("SELECT id FROM " + dataset + ".subjecttable WHERE subject='" + s + "'")
			resultSet.next()
			result = resultSet.getInt("id")
		} catch {
			case e: SqlException => println(e.getMessage)
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
		statement.close()
		result
	}

	def getSubjectIds(dataset: String): Vector[String] = {
		validateDatasetString(dataset)
		val sql = sql"""SELECT ID FROM #${dataset}.subjecttable""".as[String]
		execute(sql)
	}

	def getSubjectUris(name: String): Map[String, Int] = {
		validateDatasetString(name)
		var knownSubjects: Map[String, Int] = Map()
		try {
			val statement1 = connection.createStatement()
			val resultSet1 = statement1.executeQuery("SELECT id, subject FROM " + name + ".subjecttable")
			while (resultSet1.next()) {
				var subjectId = resultSet1.getInt("id")
				var subject = resultSet1.getString("subject")
				knownSubjects += (subject -> subjectId)
			}
			statement1.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
		knownSubjects
	}

	def getPredicateUris(name: String): Map[String, Int] = {
		validateDatasetString(name)
		var knownSubjects: Map[String, Int] = Map()
		try {
			val statement1 = connection.createStatement()
			val resultSet1 = statement1.executeQuery("SELECT id, predicate FROM " + name + ".predicatetable")
			while (resultSet1.next()) {
				var subjectId = resultSet1.getInt("id")
				var subject = resultSet1.getString("predicate")
				knownSubjects += (subject -> subjectId)
			}
			statement1.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
		knownSubjects
	}

	def getObjectUris(name: String): Map[String, Int] = {
		validateDatasetString(name)
		var knownSubjects: Map[String, Int] = Map()
		try {
			val statement1 = connection.createStatement()
			val resultSet1 = statement1.executeQuery("SELECT tuple_id, object FROM " + name + ".objecttable")
			while (resultSet1.next()) {
				var subjectId = resultSet1.getInt("tuple_id")
				var subject = resultSet1.getString("object")
				knownSubjects += (subject -> subjectId)
			}
			statement1.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
		knownSubjects
	}

	def insertObject(name: String, s: String): Int = {
		performInsert(name + ".objecttable", List("object"), List(s)) match {
			case Some(i) => i
			case None => {
				try {
					getObjectId(name, s)
				} catch {
					case e: SqlSyntaxErrorException => {
						println(e.getMessage + System.lineSeparator() + s)
						-1
					}
					case e: SqlException => {
						println(e.getMessage + System.lineSeparator() + s)
						-1
					}
				}
			}
		}
	}

	def getObjectId(name: String, s: String): Int = {
		validateDatasetString(name)
		var result: Int = -1
		val statement = connection.createStatement()
		val resultSet = statement.executeQuery("SELECT tuple_id FROM " + name + ".objecttable WHERE object='" + s.replace("'", "") + "'")
		resultSet.next()
		result = resultSet.getInt("tuple_id")
		statement.close()
		result
	}

	def getOntologyNamespace(s: String): String = {
		var namespace: String = null
		try {
			val sql = sql"""SELECT ONTOLOGY_NAMESPACE FROM PROLOD_MAIN.SCHEMATA WHERE ID = ${s}""".as[String]
			val result = execute(sql)
			result foreach ((ns) => {
				namespace = ns
			})
		} catch {
			case e: SqlException => println("error getting ontology namespace: " + e.getMessage + System.lineSeparator())
			case e: SqlSyntaxErrorException => println("syntax in ontology namespace: " + e.getMessage + System.lineSeparator())
		}
		namespace
	}

	def getNamespace(s: String): String = {
		var namespace: String = null
		try {
			val sql = sql"""SELECT NAMESPACE FROM PROLOD_MAIN.SCHEMATA WHERE ID = ${s}""".as[String]
			val result = execute(sql)
			result foreach ((ns) => {
				namespace = ns
			})
		} catch {
			case e: SqlException => println("error getting namespace: " + e.getMessage + System.lineSeparator())
			case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator())
		}
		namespace
	}

	def insertPredicate(name: String, s: String): Int = {
		performInsert(name + ".predicatetable", List("predicate"), List(s)) match {
			case Some(i) => i
			case None => getPropertyId(name, s)
		}
	}

	def insertTriples(name: String, mtObject: MaintableObject): Option[Int] = {
		var columnNames = List("subject_id", "predicate_id", "tuple_id")
		var values = List(mtObject.subjectId, mtObject.propertyId, mtObject.objectId)
		if (!mtObject.internalLink.isEmpty) {
			columnNames ::= "internallink_id"
			values ::= mtObject.internalLink.get
		}
		performInsert(name + ".maintable", columnNames, values)
		/*
		try {
			val statement = connection.createStatement()
			val resultSet = statement.execute("INSERT INTO " + name + ".subjecttable (subject) VALUES ('" + s + "')")
			println(resultSet)
		} catch {
			case e: SqlIntegrityConstraintViolationException => println(e.getMessage)
			case e: SqlException => println(e.getMessage)
		}
		*/
	}

	def insertTriplesIfNotYet(name: String, mtObject: MaintableObject): Option[Int] = {
		val s = mtObject.subjectId
		val p = mtObject.propertyId
		val o = mtObject.objectId
		var mt: Option[Int] = None
		val sql2 = sql"SELECT COUNT(*) FROM #$name.maintable WHERE subject_id = #$s AND predicate_id = #$p AND tuple_id = #$o".as[Int]
		val result = execute(sql2) map ((count) => {
			if (count == 0) {
				mt = insertTriples(name, mtObject)
			}
		})
		mt
	}

	def insertStatistics(name: String, nodes: String, links: Double, edges: Int, gcEdges: Int, gcNodes: Int, connectedcomponents: Int, stronglyconnectedcomponents: Int, highestIndegrees: String, highestOutdegrees: String) = {
		try {
			val statement = connection.createStatement()
			val resultSet = statement.execute("INSERT INTO " + name + ".graphstatistics (nodedegreedistribution, averagelinks, edges, gcnodes, gcedges, connectedcomponents, stronglyconnectedcomponents, highestIndegrees, highestOutdegrees) VALUES ('" + nodes + "'," + links + ", " + edges + ", " + gcNodes + ", " + gcEdges + ", " + connectedcomponents + ", " + stronglyconnectedcomponents + ",'" + highestIndegrees + "','" + highestOutdegrees + "')")
		} catch {
			case e: SqlIntegrityConstraintViolationException => println(e.getMessage)
			case e: SqlException => println(e.getMessage)
		}
	}

	def insertClasses(name: String, clusters: util.List[String]) = {
		val clusterUris = clusters.asScala.toList
		clusterUris.foreach {
			case (cluster) => {
				val query: String = "INSERT INTO " + name + ".clusters (label, cluster_size, username) VALUES ('" + cluster + "', 0 , 'ontology')"
				executeStringQuery(query)
			}
		}
	}

	def insertClassHierarchy(name: String, cluster_hierarchy: com.google.common.collect.Multimap[String, String]) = {
		cluster_hierarchy.asMap().asScala foreach tupled((cluster, subclusters) => {
			subclusters.asScala foreach ((subcluster) => {
				val query: String = "INSERT INTO " + name + ".cluster_hierarchy (cluster, subcluster) VALUES ('" + cluster + "', '" + subcluster + "')"
				executeStringQuery(query)
			})
		})
	}

	def executeStringQuery(query: String): Unit = {
		try {
			val statement = connection.createStatement()
			val resultSet = statement.execute(query)
		} catch {
			case e: SqlIntegrityConstraintViolationException => println(e.getMessage + System.lineSeparator() + query)
			case e: SqlException => println(e.getMessage + System.lineSeparator() + query)
			case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + query)
		}
	}

	def updateClasses(dataset: String, ontologyNamespace: String) = {
		val clusterUris = getClusterNames(dataset, ontologyNamespace)
		try {
			val sql = sql"""SELECT o.object, m.subject_id FROM #${dataset}.MAINTABLE AS m, #${dataset}.predicatetable AS p, #${dataset}.objecttable AS o WHERE m.predicate_id = p.id  AND o.tuple_id = m.tuple_id  AND p.predicate = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'""".as[(String, Int)]
			execute(sql) map tupled((clusterName, subjectId) => {
				if (!clusterUris.contains(clusterName)) {
					val query: String = "INSERT INTO " + dataset + ".clusters (label, cluster_size, username) VALUES ('" + clusterName + "', 0 , 'ontology')"
					try {
						val statement = connection.createStatement()
						val resultSet = statement.execute(query)
					} catch {
						case e: SqlIntegrityConstraintViolationException => println(e.getMessage + System.lineSeparator() + query)
						case e: SqlException => println(e.getMessage + System.lineSeparator() + query)
						case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator() + query)
					}
				}
			})
		} catch {
			case e: SqlIntegrityConstraintViolationException => println(e.getMessage + System.lineSeparator())
			case e: SqlException => println(e.getMessage + System.lineSeparator())
			case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator())
		}
	}

	def insertClusterSubjectTable(dataset: String, graphlodDataset: graphlod.dataset.Dataset) = {
		dropTable(dataset + ".CLUSTER_SUBJECTS")
		try {
			val createStatement = connection.createStatement()
			createStatement.execute("CREATE TABLE " + dataset + ".CLUSTER_SUBJECTS (CLUSTER_ID INT NOT NULL, SUBJECT_ID INT NOT NULL, PRIMARY KEY (CLUSTER_ID, SUBJECT_ID))")
			createStatement.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
		val subjectIds = getSubjectIds(dataset)
		for (subjectId <- subjectIds) {
			try {
				val sqlSubjectUri = sql"""SELECT SUBJECT FROM #${dataset}.SUBJECTTABLE WHERE ID = '#${subjectId}'""".as[(String)]
				val subjectUri = execute(sqlSubjectUri).headOption
				val classUri = graphlodDataset.getClassForSubject(subjectUri.getOrElse(""))
				val sql = sql"""SELECT ID FROM #${dataset}.CLUSTERS WHERE username = 'ontology' AND LABEL = '#${classUri}'""".as[(Int)]
				val result = execute(sql).headOption
				val clusterId = result.getOrElse(-1)
				val query: String = "INSERT INTO " + dataset + ".CLUSTER_SUBJECTS (CLUSTER_ID, SUBJECT_ID) VALUES (" + clusterId + ", " + subjectId + ")"
				val statement = connection.createStatement()
				val resultSet = statement.execute(query)
			} catch {
				case e: SqlIntegrityConstraintViolationException => println(e.getMessage)
				case e: SqlException => println(e.getMessage)
				case e: SqlSyntaxErrorException => println(e.getMessage)
			}
		}
	}

	def updatePatterns(dataset: String) = {
		validateDatasetString(dataset)
		val subjects = getSubjectUris(dataset)
		updateColoredPatterns(dataset, subjects, None)
		updateColoredPatterns(dataset, subjects, Some("_gc"))
	}

	def updateColoredPatterns(dataset: String, subjects: Map[String, Int], gc: Option[String]) = {
		val dbExt = gc.getOrElse("")
		val tableName = "COLOREDPATTERNS" + dbExt
		try {
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT id, pattern FROM " + dataset + ".COLOREDPATTERNS" + dbExt)
			var patternMap: Map[String, Int] = Map()
			while (resultSet.next()) {
				val isoId = resultSet.getInt("id")
				val pattern = resultSet.getString("pattern")
				val patternJson = Json.parse(pattern).validate[PatternFromDB].get
				var nodeList: List[Node] = Nil
				var linkList: List[Link] = Nil
				var nodeMap: Map[Int, Int] = Map()
				for (node <- patternJson.nodes) {
					var newDbId = -1
					if (subjects.contains(node.uri.get)) {
						newDbId = subjects.get(node.uri.get).get
					}
					val newNode = node.copy(dbId = Some(newDbId))
					nodeMap += (node.id -> newNode.id)
					nodeList :::= List(newNode)
				}
				for (link <- patternJson.links) {
					val newLink = link.copy()
					linkList :::= List(newLink)
				}
				val newPattern = new PatternFromDB(patternJson.name, nodeList, linkList)
				// val jsonString = Json.obj(newPattern)
				//val jsonString = Json.writes(newPattern)
				//JSON.stringify
				val jsonString = Json.toJson(newPattern).toString()
				patternMap += (jsonString -> isoId)

				//escaping in pattern?
				println("replacing " + isoId)
				val statementD = connection.createStatement()
				statementD.execute("DELETE FROM " + dataset + "." + tableName + " WHERE id = " + isoId + " AND pattern = '" + pattern + "'")
				statementD.close()

				val statementI = connection.createStatement()
				statementI.execute("INSERT INTO " + dataset + "." + tableName + " (ID, PATTERN) VALUES (" + isoId + ", '" + jsonString + "')")
				statementI.close()
			}
			statement.close()
		} catch {
			case e: SqlSyntaxErrorException => println(e.getMessage)
		}
	}

	def updateClusterSizes(dataset: String, ontologyNamespace: String) = {
		validateDatasetString(dataset)
		for (cluster: Group <- getClusters(dataset, ontologyNamespace)) {
			try {
				val objectName = ontologyNamespace + cluster.name
				val sql = sql"""SELECT COUNT(*) FROM #${dataset}.MAINTABLE AS m, #${dataset}.predicatetable AS p, #${dataset}.objecttable AS o WHERE m.predicate_id = p.id  AND o.tuple_id = m.tuple_id  AND p.predicate = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type' AND o.object = '#${objectName}'""".as[(Int)]
				val result = execute(sql)
				var clusterSize = 0
				result.foreach((cluster_size) => {
					clusterSize = cluster_size
				})

				val statement = connection.createStatement()
				statement.execute("UPDATE " + dataset + ".clusters SET cluster_size = " + clusterSize + " WHERE label = '" + ontologyNamespace + cluster.name + "'")
				statement.close()
			} catch {
				case e: SqlIntegrityConstraintViolationException => println(e.getMessage)
				case e: SqlException => println(e.getMessage + System.lineSeparator())
				case e: SqlSyntaxErrorException => println(e.getMessage + System.lineSeparator())
			}
		}
	}

	def updateSubjectCounts(dataset: String, map: Map[Int, Int]) = {
		map.foreach {
			case (key, value) => {
				val statement = connection.createStatement()
				statement.execute("UPDATE " + dataset + ".SUBJECTTABLE SET cnt = " + value + " WHERE id = " + key)
			}
		}
	}

	def updatePropertyCounts(dataset: String, map: Map[Int, Int]) = {
		map.foreach {
			case (key, value) => {
				val statement = connection.createStatement()
				statement.execute("UPDATE " + dataset + ".PREDICATETABLE SET cnt = " + value + " WHERE id = " + key)
			}
		}
	}

	class RsIterator(rs: ResultSet) extends Iterator[ResultSet] {
		def hasNext: Boolean = rs.next()

		def next(): ResultSet = rs
	}

}
