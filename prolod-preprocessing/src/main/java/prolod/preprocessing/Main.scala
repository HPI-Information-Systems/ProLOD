package prolod.preprocessing

import graphlod.algorithms.GraphFeatures
import graphlod.dataset.Dataset

import prolod.common.Configuration

import scala.collection.JavaConversions._

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

        val dataset = Dataset.fromFiles(datasetFiles, namespace, ontns, excludedNamespaces)
        val graphFeatures = new GraphFeatures("main_graph", dataset.getGraph, dataset.getSimpleGraph)

        System.out.println("Hello World" + Configuration.foo);    }
}
