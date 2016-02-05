package prolod.preprocessing

import graphlod.dataset.Dataset
import prolod.common.config.DatabaseConnection

class GraphLodSatelliteComponentAnalysis(var db: DatabaseConnection, var name : String, var dataset: Dataset) {
	//val excludedNamespaces : List[String] = Nil
	val graphLodPatternSimilarity : graphlod.SatelliteComponentAnalysis = new graphlod.SatelliteComponentAnalysis(name, dataset)

	def run: Unit = {
		val graphLodInstance: GraphLOD = graphLod.getGraphLodInstance
		logger.info("Insert patterns into DB")
		db.insertPatterns(name, graphLodInstance.patterns, graphLodInstance.coloredPatterns, graphLodInstance.colorIsomorphicPatterns, graphLodInstance.patternDiameter, None)
	}
}
