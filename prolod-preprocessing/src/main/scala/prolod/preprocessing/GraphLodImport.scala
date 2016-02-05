package prolod.preprocessing

import graphlod.GraphLOD
import graphlod.dataset.Dataset
import prolod.common.config.DatabaseConnection

class GraphLodImport(var db: DatabaseConnection, var name : String, namespace: String, var ontologyNamespace : String, dataset: Dataset, subjects: Map[String, Int]) {
	val skipChromatic: Boolean = true
	val skipGraphviz: Boolean = true
	val minImportantSubgraphSize: Int = 3
	val importantDegreeCount: Int = 3

	val graphLod : GraphLOD = GraphLOD.loadDataset(name, dataset)

	def run: Unit = {
		db.insertDataset(name, graphLod.graphFeatures.getVertexCount, graphLod.graphFeatures.getVertexCount, ontologyNamespace, namespace)
		db.insertClasses(name, graphLod.dataset.getOntologyClasses)
		db.insertClassHierarchy(name, graphLod.dataset.getOntologySubclasses)
		db.insertStatistics(name, graphLod.nodeDegreeDistribution.toString, graphLod.averageLinks,
			graphLod.nodes, graphLod.edges, graphLod.gcNodes, graphLod.gcEdges, graphLod.connectedGraphs.size,
			graphLod.stronglyConnectedGraphs.size, graphLod.highestIndegrees.toString, graphLod.highestOutdegrees.toString)
		db.insertPatterns(name, graphLod.patterns, graphLod.coloredPatterns, graphLod.colorIsomorphicPatterns, graphLod.patternDiameter, subjects, None)
		db.insertPatternsGC(name, graphLod.patternsGC, graphLod.coloredPatternsGC, graphLod.colorIsomorphicPatternsGC, graphLod.patternDiameterGC, subjects)
	}


	//db.insertStats(name, graphLod)

	/*
	db.getDatasets()
	db.insert
    */

	/*
	val dataset = Dataset.fromFiles(files.asJava, name, namespace, ontologyNamespace, excludedNamespaces.asJava)
	val graphFeatures = new GraphFeatures("main_graph", dataset.getGraph, dataset.getSimpleGraph)
    */
}
