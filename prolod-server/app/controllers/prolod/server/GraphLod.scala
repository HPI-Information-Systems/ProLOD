package controllers.prolod.server

import prolod.common.config.{DatabaseConnection, Configuration}
import prolod.common.models._
import GraphLodResultFormats.graphLodResultFormat

import play.api.libs.json._
import play.api.Logger
import play.api.mvc.{Action, Controller}
import prolod.common.models.PatternFormats.patternFormat
import prolod.common.models.PatternFormats.patternDBFormat

object GraphLod extends Controller {
  def getGraphStatistics(datasetId: String, groups: List[Int] = List()) = Action {
    var config = new Configuration()
    var db = new DatabaseConnection(config)
    val patternList: List[Pattern] = db.getPatterns(datasetId)

    val data: GraphLodResult = GraphLodResult(datasetId)

    val statistics = db.getStatistics(datasetId)

    //data.nodes = 2000
    data.edges = statistics.get("edges").get.toInt
    // data.connectedComponents = 123
    // data.stronglyConnectedComponents = 100
    data.patterns = patternList
    //var nodeDegreeDistribution = statistics.get("nodeDegreeDistribution").get
    // val nodeDegreeDistributionMap = Json.parse(nodeDegreeDistribution).as[Map[Int, Int]]
    // nodeDegreeDistributionMap
    data.nodeDegreeDistribution =  Map(1 -> 12, 2 -> 5, 3 -> 41, 5 -> 2, 21 -> 8, 23 -> 4)

    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getGraphPatternStatistics(datasetId: String, groups: List[Int], pattern: Int) = Action {
    var config = new Configuration()
    var db = new DatabaseConnection(config)
    val patternList: List[Pattern] = db.getPatterns(datasetId)

    val data: GraphLodResult = GraphLodResult(datasetId)
    data.nodes = 2000
    data.edges = 1000
    data.patterns = patternList
    data.nodeDegreeDistribution = Map(1 -> 12, 2 -> 5, 3 -> 41, 5 -> 2, 21 -> 8, 23 -> 4)

    val json = Json.obj("statistics" -> data)

    Ok(json)
  }

  def getBigComponent(dataset: String, groups: List[Int], pattern: Int) = Action {
    Ok("this is big!")
  }
}
