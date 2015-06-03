package de.hpi.prolod.common.db

import scala.collection.immutable.HashMap
import scala.util.parsing.json.JSONObject

case class GraphLodResult(val datasetId : Integer) {
	val nodes : Int = 0
	val edges : Int = 0

	val connectedComponents : Int = 0
	val connectedComponentsMinEdges : Int = 0
	val connectedComponentsMaxEdges : Int = 0
	val connectedComponentsAvgEdges : Float = 0

	val stronglyConnectedComponents : Int = 0
	val stronglyConnectedComponentsMinEdges : Int = 0
	val stronglyConnectedComponentsMaxEdges : Int = 0
	val stronglyConnectedComponentsAvgEdges : Int = 0

	val averageDiameter : Float = 0

	val nodeDegreeDistribution : HashMap[Int, Int] = new HashMap()

	val patternJson : List[JSONObject] = Nil

	val giantComponentEdges : Int = 0
	val giantComponentNodes : Int = 0
	val giantComponentDiameter : Float = 0
}

object GraphLodResult {
	def load(datasetId: Integer) = {
		new GraphLodResult(datasetId)
	}
}
