package controllers.prolod.server

import prolod.common.models.{Pattern, GraphLodResultFormats, GraphLodResult}
import GraphLodResultFormats.graphLodResultFormat

import play.api.libs.json._
import play.api.Logger
import play.api.mvc.{Action, Controller}
import prolod.common.models.PatternFormats.patternFormat
import prolod.common.models.Pattern

object GraphLod extends Controller {
  def getGraphStatistics(datasetId: Int, groups: List[Int] = List()) = Action {

    val p1 = Json.parse("{\"id\": 1, \"name\": \"thing1\", \"occurences\":100,\"nodes\":[{\"id\":1},{\"id\":2},{\"id\":3}],\"links\":[{\"source\":1,\"target\":3},{\"source\":2,\"target\":1}]}").validate[Pattern]
    val p2 = Json.parse("{\"id\": 2, \"name\": \"thing2\", \"occurences\":7,\"nodes\":[{\"id\":1},{\"id\":2},{\"id\":3}],\"links\":[{\"source\":1,\"target\":3},{\"source\":2,\"target\":1}]}").validate[Pattern]
    val patterns = List(p1, p2).filter(p => p.isSuccess).map(p => p.get)
    val errors = List(p1, p2).filter(p => p.isError)
    if (errors.nonEmpty) {
      Logger.warn("Could not validate " + errors)
    }

    val data: GraphLodResult = GraphLodResult(0)
    data.nodes = 2000
    data.edges = 1000
    data.patterns = patterns
    data.nodeDegreeDistribution = Map(1 -> 12, 2 -> 5, 3 -> 41, 5 -> 2, 21 -> 8, 23 -> 4)

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
  def getGraphPatternStatistics(dataset: Int, groups: List[Int], pattern: Int) = Action {

    val p1 = Json.parse("{\"id\": 1, \"name\": \"thing1\", \"occurences\":100,\"nodes\":[{\"id\":1, \"group\": \"1\"},{\"id\":2, \"group\": \"2\"},{\"id\":3, \"group\": \"2\"}],\"links\":[{\"source\":1,\"target\":3},{\"source\":2,\"target\":1}]}").validate[Pattern]
    val patterns = List(p1).filter(p => p.isSuccess).map(p => p.get)
    val errors = List(p1).filter(p => p.isError)
    if (errors.nonEmpty) {
      Logger.warn("Could not validate " + errors)
    }

    val data: GraphLodResult = GraphLodResult(0)
    data.nodes = 2000
    data.edges = 1000
    data.patterns = patterns
    data.nodeDegreeDistribution = Map(1 -> 12, 2 -> 5, 3 -> 41, 5 -> 2, 21 -> 8, 23 -> 4)

    val json = Json.obj("statistics" -> data)

    Ok(json)
  }

  def getBigComponent(dataset: Int, groups: List[Int], pattern: Int) = Action {
    Ok("this is big!")
  }
}
