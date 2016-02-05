package prolod.preprocessing

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

class AddSimilarPatterns(name: String, dataset: Dataset) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	val graphLodPatternSimilarity = new GraphLodPatternSimilarity(db, name, dataset)
	graphLodPatternSimilarity.run
}

class SatelliteComponentAnalysis(name: String, dataset: Dataset) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	val graphLodPatternSimilarity = new GraphLodSatelliteComponentAnalysis(db, name, dataset)
	graphLodPatternSimilarity.run
}

class ImportDataset(name: String, namespace: String, ontologyNamespace: String, dataset: Dataset, importTriplesFlag: Boolean, keyness: Boolean, addFiles: Boolean) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	var subjectsKnown: Map[String, Int] = Map()

	if (addFiles) {
		new TripleImporter(db, name, namespace, ontologyNamespace, subjectsKnown, List()).importTriples(true)
		db.updateClasses(name, ontologyNamespace)
		db.updateClusterSizes(name, ontologyNamespace)
	} else {
		if (!keyness) {
			// graphlod & keyness
			val graphlod = new GraphLodImport(db, name, namespace, ontologyNamespace, dataset, subjectsKnown)

			if (importTriplesFlag) {
				db.dropMainTables(name)
			}

			db.dropTables(name)
			db.createTables(name)

			if (importTriplesFlag) {
				/*
				var importTripleActor: ImportTripleActor = new ImportTripleActor()
				importTripleActor.start
				*/
				new TripleImporter(db, name, namespace, ontologyNamespace, subjectsKnown, List()).importTriples(false)
			} else {
				subjectsKnown = db.getSubjectUris(name)
			}

			graphlod.run

			if (importTriplesFlag) {
				db.createIndices(name)
			}

			db.insertClusterSubjectTable(name, graphlod.graphLod.dataset)

			db.updateClusterSizes(name, ontologyNamespace)
			val keynessImporter = new KeynessImport(db, name)
			keynessImporter.run
		} else {
			// only keyness
			val keynessImporter = new KeynessImport(db, name)
			keynessImporter.run
		}
	}
}

object ImportDataset {
	def main(args: Array[String]) {
		args match {
			case Array("importTriples", name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new ImportDataset(name, namespace, ontologyNamespace, dataset, true, false, false)
			}
			case Array("importTriples", name, namespace, ontologyNamespace, excludeNS, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new ImportDataset(name, namespace, ontologyNamespace, dataset, true, false, false)
			}
			case Array("updateClusterSizes", name, ontologyNamespace) => {
				new UpdateClusters(name, ontologyNamespace)
			}
			case Array("addImport", name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new ImportDataset(name, namespace, ontologyNamespace, dataset, false, false, true)
			}
			case Array("updatePatternsWithIds", name) => {
				new UpdatePatterns(name)
			}
			case Array("keyness", name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new ImportDataset(name, namespace, ontologyNamespace, dataset, false, true, false)
			}
			case Array("keyness", name, namespace, ontologyNamespace, excludeNS, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new ImportDataset(name, namespace, ontologyNamespace, dataset, false, true, false)
			}
			case Array("similarPatterns", name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new AddSimilarPatterns(name, dataset)
			}
			case Array("similarPatterns", name, namespace, ontologyNamespace, excludeNS, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new AddSimilarPatterns(name, dataset)
			}
			case Array("satelliteComponents", name, namespace, ontologyNamespace, files) => {
				val dataset: Dataset = createDataset(files, name, namespace, files, Some(ontologyNamespace))
				new SatelliteComponentAnalysis(name, dataset)
			}
			case Array("SWTGraphML", name, file) => {
				val dataset = Dataset.fromGraphML(file, name, new SWTGraphMLHandler())
				new ImportDataset(name, "", "", dataset, false, false, false)
			}
			case Array(name, namespace, ontologyNamespace, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, None)
				new ImportDataset(name, namespace, ontologyNamespace, dataset, false, false, false)
			}
			case Array(name, namespace, ontologyNamespace, excludeNS, files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new ImportDataset(name, namespace, ontologyNamespace, dataset, false, false, false)
			}
			case Array(name, namespace, ontologyNamespace, "-excludeNS", excludeNS, "-files", files) => {
				val dataset = createDataset(name, namespace, ontologyNamespace, files, Some(excludeNS))
				new ImportDataset(name, namespace, ontologyNamespace, dataset, false, false, false)
			}
			case _ => printUsage()
		}
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
