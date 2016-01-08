package prolod.preprocessing

import java.util

import graphlod.SatelliteComponentAnalysis
import prolod.common.config.DatabaseConnection

import scala.collection.JavaConverters._

class GraphLodSatelliteComponentAnalysis(var db: DatabaseConnection, var name : String, namespace: String, var ontologyNamespace : String, excludedNamespaces : List[String], files: List[String]) {
	//val excludedNamespaces : List[String] = Nil
	val datasetFilesJava: util.List[String] = files.asJava

	val graphLodPatternSimilarity : graphlod.SatelliteComponentAnalysis = new graphlod.SatelliteComponentAnalysis(name, files.asJava, namespace, ontologyNamespace, excludedNamespaces.asJava)

	def run: Unit = {
		// db.insertSimilarPatterns(name, graphLodPatternSimilarity.similarityLists, graphLodPatternSimilarity.similarityPaths, graphLodPatternSimilarity.differenceToFirstElement)
	}
}
