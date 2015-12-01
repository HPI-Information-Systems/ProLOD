package prolod.preprocessing

import java.util

import graphlod.{ArgumentParser, EdgeSimilarity}
import prolod.common.config.DatabaseConnection
import scala.collection.JavaConverters._

class GraphLodPatternSimilarity(var db: DatabaseConnection, var name : String, namespace: String, var ontologyNamespace : String, excludedNamespaces : List[String], files: List[String]) {
	//val excludedNamespaces : List[String] = Nil
	val datasetFilesJava: util.List[String] = files.asJava

	val graphLodPatternSimilarity : EdgeSimilarity = new EdgeSimilarity(name, files.asJava, namespace, ontologyNamespace, excludedNamespaces.asJava)

	def run: Unit = {
		db.insertSimilarPatterns(name, graphLodPatternSimilarity.similarityLists, graphLodPatternSimilarity.similarityPaths, graphLodPatternSimilarity.differenceToFirstElement)
	}
}
