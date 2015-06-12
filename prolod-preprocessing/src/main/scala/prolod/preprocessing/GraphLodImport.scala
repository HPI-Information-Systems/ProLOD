package prolod.preprocessing

import java.util

import graphlod.GraphLOD
import graphlod.algorithms.GraphFeatures
import graphlod.dataset.Dataset
import prolod.common.config.DatabaseConnection
import scala.collection.JavaConverters._

class GraphLodImport(var db: DatabaseConnection, name : String, namespace: String, ontologyNamespace : String, excludedNamespaces : List[String], files : List[String]) {
	val skipChromatic: Boolean = true
	val skipGraphviz: Boolean = true
	val minImportantSubgraphSize: Int = 3
	val importantDegreeCount: Int = 3

	//val excludedNamespaces : List[String] = Nil
	val datasetFilesJava: util.List[String] = files.asJava

	val graphLod : GraphLOD = GraphLOD.loadDataset(name, files.asJava, namespace, ontologyNamespace, excludedNamespaces.asJava)
	// TODO tuples
	db.insertDataset(name, graphLod.graphFeatures.getVertexCount, graphLod.graphFeatures.getVertexCount)
	db.insertPatterns(name, graphLod.patterns, graphLod.coloredPatterns)
	var connectedGraphSizes = graphLod.connectedGraphSizes.asScala.toList.max[Integer]
	db.insertStatistics(name, graphLod.nodeDegreeDistribution.toString, graphLod.averageLinks, graphLod.graphFeatures.getEdgeCount, connectedGraphSizes, graphLod.connectedGraphs.size, graphLod.stronglyConnectedGraphs.size())
	db.insertClasses(name, graphLod.dataset.ontologyClasses)

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
