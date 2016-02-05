package prolod.preprocessing

import graphlod.dataset.Dataset
import prolod.common.config.DatabaseConnection

class GraphLodSatelliteComponentAnalysis(var db: DatabaseConnection, var name : String, var dataset: Dataset) {
	//val excludedNamespaces : List[String] = Nil
	val graphLodPatternSimilarity : graphlod.SatelliteComponentAnalysis = new graphlod.SatelliteComponentAnalysis(name, dataset)

	def run: Unit = {
		// db.insertSimilarPatterns(name, graphLodPatternSimilarity.similarityLists, graphLodPatternSimilarity.similarityPaths, graphLodPatternSimilarity.differenceToFirstElement)
	}
}
