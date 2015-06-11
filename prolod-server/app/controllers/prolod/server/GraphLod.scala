package controllers.prolod.server

import prolod.common.models.{Pattern, GraphLodResultFormats, GraphLodResult}
import GraphLodResultFormats.graphLodResultFormat

import play.api.libs.json._
import play.api.Logger
import play.api.mvc.{Action, Controller}
import prolod.common.models.PatternFormats.patternFormat
import prolod.common.models.Pattern

object GraphLod extends Controller {
  def getGraphStatistics(datasetId: String, groups: List[Int] = List()) = Action {

    val p1 = Json.parse("{\"id\": 1, \"name\": \"thing1\", \"occurences\":100,\"nodes\":[{\"id\":1},{\"id\":2},{\"id\":3}],\"links\":[{\"source\":1,\"target\":3},{\"source\":2,\"target\":1}]}").validate[Pattern]
    val p2 = Json.parse("{\"id\": 2, \"name\": \"thing2\", \"occurences\":7,\"nodes\":[{\"id\":1},{\"id\":2},{\"id\":3}],\"links\":[{\"source\":1,\"target\":3},{\"source\":2,\"target\":1}]}").validate[Pattern]
    val p3 = Json.parse("{\"id\": 3, \"name\": \"thing3\", \"occurences\":10,\"nodes\":[{\"id\":1},{\"id\":2},{\"id\":3},{\"id\":4},{\"id\":5},{\"id\":6},{\"id\":7},{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11}],\"links\":[{\"source\":4,\"target\":3},{\"source\":4,\"target\":2},{\"source\":4,\"target\":1},{\"source\":4,\"target\":6},{\"source\":4,\"target\":9},{\"source\":4,\"target\":7},{\"source\":5,\"target\":3},{\"source\":5,\"target\":2},{\"source\":5,\"target\":1},{\"source\":5,\"target\":6},{\"source\":5,\"target\":9},{\"source\":5,\"target\":11},{\"source\":7,\"target\":3},{\"source\":8,\"target\":3},{\"source\":10,\"target\":3},{\"source\":10,\"target\":2},{\"source\":10,\"target\":1},{\"source\":10,\"target\":6},{\"source\":10,\"target\":9},{\"source\":10,\"target\":8},{\"source\":11,\"target\":3}]}").validate[Pattern]
    val p4 = Json.parse("{\"id\": 4, \"name\": \"thing4\", \"occurences\":10,\"nodes\":[{\"id\":1},{\"id\":2},{\"id\":3},{\"id\":4},{\"id\":5},{\"id\":6},{\"id\":7},{\"id\":8},{\"id\":9},{\"id\":10},{\"id\":11},{\"id\":12},{\"id\":13},{\"id\":14},{\"id\":15},{\"id\":16}],\"links\":[{\"source\":4,\"target\":1},{\"source\":5,\"target\":1},{\"source\":5,\"target\":2},{\"source\":5,\"target\":8},{\"source\":5,\"target\":11},{\"source\":6,\"target\":1},{\"source\":6,\"target\":2},{\"source\":6,\"target\":8},{\"source\":6,\"target\":10},{\"source\":7,\"target\":14},{\"source\":7,\"target\":8},{\"source\":7,\"target\":16},{\"source\":9,\"target\":1},{\"source\":9,\"target\":2},{\"source\":9,\"target\":8},{\"source\":9,\"target\":4},{\"source\":10,\"target\":1},{\"source\":11,\"target\":1},{\"source\":12,\"target\":1},{\"source\":12,\"target\":3},{\"source\":13,\"target\":1},{\"source\":13,\"target\":2},{\"source\":13,\"target\":8},{\"source\":13,\"target\":12},{\"source\":15,\"target\":3},{\"source\":15,\"target\":8},{\"source\":15,\"target\":12},{\"source\":16,\"target\":13}]}").validate[Pattern]
        
  val patterns = List(p1, p2, p3, p4).filter(p => p.isSuccess).map(p => p.get)
    val errors = List(p1, p2, p3, p4).filter(p => p.isError)
    if (errors.nonEmpty) {
      Logger.warn("Could not validate " + errors)
    }

    val data: GraphLodResult = GraphLodResult(0)
    data.nodes = 2000
    data.edges = 1000
    data.connectedComponents = 123
    data.stronglyConnectedComponents = 100
    data.patterns = patterns
    data.nodeDegreeDistribution = Map(1 -> 12, 2 -> 5, 3 -> 41, 5 -> 2, 21 -> 8, 23 -> 4)

    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getGraphPatternStatistics(dataset: String, groups: List[Int], pattern: Int) = Action {

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

  def getBigComponent(dataset: String, groups: List[Int], pattern: Int) = Action {
    Ok("this is big!")
  }
}
