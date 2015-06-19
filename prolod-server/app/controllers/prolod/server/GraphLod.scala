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
  def getGraphStatistics(datasetId: String, groups: List[String]) = Action {
    println("groups:" + groups)
    val config = new Configuration()
    val db = new DatabaseConnection(config)
    val patternList: List[Pattern] = db.getPatterns(datasetId)
    val data: GraphLodResult = GraphLodResult(datasetId)
    val statistics = db.getStatistics(datasetId)

    data.nodes = db.getDatasetEntities(datasetId)
    data.edges = statistics.get("edges").get.toInt
    data.giantComponentEdges = statistics.get("gcnodes").get.toInt
    data.patterns = patternList
    data.connectedComponents = statistics.get("connectedcomponents").get.toInt
    data.stronglyConnectedComponents = statistics.get("stronglyconnectedcomponents").get.toInt
    val nodeDegreeDistributionMap = Json.parse(statistics.get("nodedegreedistribution").get).as[Map[Int, Int]]
    data.nodeDegreeDistribution =  nodeDegreeDistributionMap

    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getGraphDistribution(pattern: List[Pattern]): Map[String, Int] = {


    return Map()
  }

  def getGraphPatternStatistics(datasetId: String, groups: List[String], pattern: Int) = Action {
    var config = new Configuration()
    var db = new DatabaseConnection(config)
    val data: GraphLodResult = GraphLodResult(datasetId)
    val patternList: List[Pattern] = db.getColoredPatterns(datasetId, pattern)
    if (groups.size > 0) {
      var newPatternList: List[Pattern] = Nil
      for (pattern : Pattern <- patternList) {
        var newNodes: List[Node] = Nil
        for (node : Node <- pattern.nodes) {
          var newNode : Node = node
          if (!groups.contains(node.group.getOrElse(""))) {
            newNode = new Node(node.id, node.uri, None)
          }
          newNodes ::= newNode
        }
        newPatternList ::=new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links)
      }
      data.patterns = newPatternList
    } else {
      data.patterns = patternList
    }
    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getBigComponent(dataset: String, groups: List[String], pattern: Int) = Action {
    Ok("this is big!")
  }
}
