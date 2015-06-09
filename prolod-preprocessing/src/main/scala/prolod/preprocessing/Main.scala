package prolod.preprocessing

import com.typesafe.config.ConfigFactory
import graphlod.algorithms.GraphFeatures
import graphlod.dataset.Dataset


object Main {
    def main(args: Array[String]) {
        val name: String = "name"
        val datasetFiles  = List("a")
        val skipChromatic: Boolean = true
        val excludedNamespaces = List("ns")
        val skipGraphviz: Boolean = true
        val namespace: String = "ns"
        val ontns: String = "ontns"
        val minImportantSubgraphSize: Int = 3
        val importantDegreeCount: Int = 3

        // val dataset = Dataset.fromFiles(datasetFiles, namespace, ontns, excludedNamespaces)
        // val graphFeatures = new GraphFeatures("main_graph", dataset.getGraph, dataset.getSimpleGraph)

        var config = new Configuration()
        DatabaseConnection.main(config)
    }
}
