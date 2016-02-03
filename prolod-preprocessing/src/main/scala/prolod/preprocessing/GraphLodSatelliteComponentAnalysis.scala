package prolod.preprocessing

import java.util

import com.typesafe.scalalogging.LazyLogging
import graphlod.SatelliteComponentAnalysis
import graphlod.GraphLOD
import prolod.common.config.DatabaseConnection

import scala.collection.JavaConverters._

class GraphLodSatelliteComponentAnalysis(var db: DatabaseConnection, var name : String, namespace: String, var ontologyNamespace : String, excludedNamespaces : List[String], files: List[String]) extends LazyLogging {
	val datasetFilesJava: util.List[String] = files.asJava

	val graphLod : graphlod.SatelliteComponentAnalysis = new graphlod.SatelliteComponentAnalysis(name, files.asJava, namespace, ontologyNamespace, excludedNamespaces.asJava)

	def run: Unit = {
		val graphLodInstance: GraphLOD = graphLod.getGraphLodInstance
		logger.info("Insert patterns into DB")
		db.insertPatterns(name, graphLodInstance.patterns, graphLodInstance.coloredPatterns, graphLodInstance.colorIsomorphicPatterns, graphLodInstance.patternDiameter, None)
	}
}
