package controllers.prolod.server

import de.hpi.prolod.common.db.GraphLodResult
import graphlod.algorithms.GraphFeatures
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object GraphLod extends Controller {

	def getGraphStatistics(datasetId: Integer) = Action {
		val data: GraphLodResult = new GraphLodResult(datasetId)
		implicit val json = Json.format[GraphLodResult]
		//val json = Json.format[GraphLodResult] // Json.obj("graphStatistics" -> data) // Json.format[
		Ok(json)
	}

	def getGraphPatternStatistics(datasetId: Integer, pattern: Integer) = Action {
		// TODO
		//new GraphFeatures()
		val data: GraphLodResult = new GraphLodResult(datasetId)
		implicit val json = Json.format[GraphLodResult]
		Ok(json)
	}
}
