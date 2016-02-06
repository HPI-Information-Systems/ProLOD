package prolod.preprocessing

import com.typesafe.scalalogging.LazyLogging
import graphlod.dataset.{Dataset, SWTGraphMLHandler}
import prolod.common.config.{Configuration, DatabaseConnection}

import scala.collection.JavaConverters._

class UpdateClusters(name: String, ontologyNamespace: String) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	db.updateClasses(name, ontologyNamespace)
	db.updateClusterSizes(name, ontologyNamespace)
}

class UpdatePatterns(name: String) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	db.updatePatterns(name)
}

class EdgeSetSimilarityImport(name: String, dataset: Dataset) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	val graphLodPatternSimilarity = new GraphLodPatternSimilarity(db, name, dataset)
	graphLodPatternSimilarity.run
}

class SatelliteComponentAnalysis(name: String, dataset: Dataset) extends LazyLogging {
	logger.info("Analyze satellite components and import into DB")
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	val graphLodSatelliteComponentAnalysis = new GraphLodSatelliteComponentAnalysis(db, name, dataset)
	graphLodSatelliteComponentAnalysis.run
}

class ImportDataset(name: String, namespace: String, ontologyNamespace: String, dataset: Dataset) extends LazyLogging {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	val subjectsKnown = db.getSubjectUris(name)

	// graphlod & keyness
	val graphlod = new GraphLodImport(db, name, namespace, ontologyNamespace, dataset, subjectsKnown)

	db.dropTables(name)
	db.createTables(name)

	graphlod.run

	db.insertClusterSubjectTable(name, graphlod.graphLod.dataset)

	db.updateClusterSizes(name, ontologyNamespace)
	val keynessImporter = new KeynessImport(db, name)
	keynessImporter.run
}

object ImportDataset {
	//noinspection RedundantBlock
	def main(args: Array[String]) {
		args match {
			case Array("importTriples", name, namespace, ontologyNamespace, files) => {
				val db = createDb()
				db.dropMainTables(name)
				new TripleImporter(db, name, namespace, ontologyNamespace, List(files)).importTriples(false)
				db.createIndices(name)
			}
			case Array("importTriples", name, namespace, ontologyNamespace, excludeNS, files) => {
				val db = createDb()
				db.dropMainTables(name)
				new TripleImporter(db, name, namespace, ontologyNamespace, List(files)).importTriples(false)
				db.createIndices(name)
			}
			case Array("addImport", name, namespace, ontologyNamespace, files) => {
					val db: DatabaseConnection = createDb()
				new TripleImporter(db, name, namespace, ontologyNamespace, List(files)).importTriples(true)
				db.updateClasses(name, ontologyNamespace)
				db.updateClusterSizes(name, ontologyNamespace)
			}
			case Array("updateClusterSizes", name, ontologyNamespace) => {
				new UpdateClusters(name, ontologyNamespace)
			}
			case Array("updatePatternsWithIds", name) => {
				new UpdatePatterns(name)
			}
			case Array("keyness", name, namespace, ontologyNamespace, files) => {
				// only keyness
				val db: DatabaseConnection = createDb()
				new KeynessImport(db, name).run
			}
			case Array("keyness", name, namespace, ontologyNamespace, excludeNS, files) => {
				// only keyness
				val db: DatabaseConnection = createDb()
				new KeynessImport(db, name).run
			}
			case Array("similarPatterns", name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new EdgeSetSimilarityImport(name, dataset)
			}
			case Array("similarPatterns", name, namespace, ontologyNamespace, excludeNS, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new EdgeSetSimilarityImport(name, dataset)
			}
			case Array("satelliteComponents", name, namespace, ontologyNamespace, files) => {
				val dataset: Dataset = createDataset(files, name, namespace, files, Some(ontologyNamespace))
				new SatelliteComponentAnalysis(name, dataset)
			}
			case Array("SWTGraphML", name, file) => {
				val dataset = Dataset.fromGraphML(file, name, new SWTGraphMLHandler())
				new ImportDataset(name, "", "", dataset)
			}
			case Array(name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new ImportDataset(name, namespace, ontologyNamespace, dataset)
			}
			case Array(name, namespace, ontologyNamespace, excludeNS, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new ImportDataset(name, namespace, ontologyNamespace, dataset)
			}
			case Array(name, namespace, ontologyNamespace, "-excludeNS", excludeNS, "-files", files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new ImportDataset(name, namespace, ontologyNamespace, dataset)
			}
			case _ => printUsage()
		}
	}

	def createDb(): DatabaseConnection = {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		db
	}

	private def createDataset(name: String, namespace: String, ontologyNamespace: String, files: String, excludeNS: Option[String]): Dataset = {
		val datasetFiles: List[String] = if (files.isEmpty) Nil else List(files)
		val excludeNamespaces: List[String] = if (excludeNS.isEmpty) Nil else List(excludeNS.get)
		val dataset: Dataset = Dataset.fromFiles(datasetFiles.asJava, name, namespace, ontologyNamespace, excludeNamespaces.asJava)
		dataset
	}

	private def printUsage() {
		println("usage:")
		println("  [importTriples|updateClusterSizes|addImport|keyness|updatePatternsWithIds|similarPatterns] name namespace ontologyNamespace [files]")
		println("  name namespace ontologyNamespace [excludeNamespaces] [files]")
	}

}
