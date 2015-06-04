package controllers.prolod.server

import model.prolod.server.GraphLodResult
import model.prolod.server.GraphLodResultFormats._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object GraphLod extends Controller {
	def getGraphStatistics(datasetId: Int) = Action {
/*		val data: GraphLodResult = GraphLodResult(datasetId)
		                                //Json.format[GraphLodResult]
		// val json = Json.obj("datasets" -> Json.format[GraphLodResult])
           val json = Json.format[GraphLodResult]
//		val json = Json.toJson(graphlodResult)
Ok(Json.toJson(json))
*/
		var data: GraphLodResult = GraphLodResult(0)
		data.nodes = 2000
		data.edges = 1000

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	/*
	def getGraphPatternStatistics(datasetId: Integer, pattern: Integer) = Action {
		//new GraphFeatures()
		val json = Json.toJson(graphlodResult)
		Ok(json)
	}
	*/
}
