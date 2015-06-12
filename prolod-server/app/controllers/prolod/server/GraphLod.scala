package controllers.prolod.server

import prolod.common.config.{DatabaseConnection, Configuration}
import prolod.common.models._
import GraphLodResultFormats.graphLodResultFormat
import prolod.common.models.GraphLodResultFormats.mapIntIntFormat

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

    data.nodes = db.getDatasetEntities(datasetId)
    data.edges = statistics.get("edges").get.toInt
    // data.connectedComponents = 123
    // data.stronglyConnectedComponents = 100
    data.patterns = patternList
    var nodeDegreeDistribution = statistics.get("nodedegreedistribution").get
    val nodeDegreeDistributionMap = Json.parse(nodeDegreeDistribution).as[Map[Int, Int]]
    data.nodeDegreeDistribution =  nodeDegreeDistributionMap

    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getGraphPatternStatistics(datasetId: String, groups: List[Int], pattern: Int) = Action {
    var config = new Configuration()
    var db = new DatabaseConnection(config)
    val patternList: List[Pattern] = db.getColoredPatterns(datasetId, pattern)

    val data: GraphLodResult = GraphLodResult(datasetId)
    data.patterns = patternList
    val json = Json.obj("statistics" -> data)

    Ok(json)
  }

  def getBigComponent(dataset: String, groups: List[Int], pattern: Int) = Action {
    Ok("this is big!")
  }
}
