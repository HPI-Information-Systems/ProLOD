package prolod.preprocessing

import graphlod.EdgeSimilarity
import graphlod.dataset.Dataset
import prolod.common.config.DatabaseConnection

class GraphLodPatternSimilarity(var db: DatabaseConnection, var name : String, dataset: Dataset) {

	val graphLodPatternSimilarity : EdgeSimilarity = new EdgeSimilarity(name, dataset)

	def run: Unit = {
		db.insertSimilarPatterns(name, graphLodPatternSimilarity.similarityLists, graphLodPatternSimilarity.similarityPaths, graphLodPatternSimilarity.differenceToFirstElement)
	}
}
