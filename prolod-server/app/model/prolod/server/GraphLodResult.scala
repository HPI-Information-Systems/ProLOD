package model.prolod.server

import play.api.libs.json.{Json, Writes}

import java.util.HashMap

case class GraphLodResult(datasetId : Int,
	                      var nodes : Int = 0,
	                      var edges : Int = 0,
	                      var connectedComponents : Int = 0,
	                      var connectedComponentsMinEdges : Int = 0,
	                      var connectedComponentsMaxEdges : Int = 0,
	                      var connectedComponentsAvgEdges : Float = 0,
	                      var stronglyConnectedComponents : Int = 0,
	                      var stronglyConnectedComponentsMinEdges : Int = 0,
	                      var stronglyConnectedComponentsMaxEdges : Int = 0,
	                      var stronglyConnectedComponentsAvgEdges : Int = 0,
	                      var averageDiameter : Float = 0,
	                      var giantComponentEdges : Int = 0,
	                      var giantComponentNodes : Int = 0,
	                      var giantComponentDiameter : Float = 0,
							var patterns : List[Pattern] = Nil

							// val nodeDegreeDistribution : HashMap[Int, Int] = new HashMap()
                          // val patternJson : HashMap[JSONObject, Integer] = new HashMap()

	                         ) {

}

object GraphLodResult {
	def load(datasetId: Integer) = {
		new GraphLodResult(datasetId)
	}
}

object GraphLodResultFormats {
	implicit val graphLodResultFormat = Json.format[GraphLodResult]
}
