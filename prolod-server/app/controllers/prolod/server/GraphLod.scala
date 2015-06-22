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
import prolod.common.models.GraphLodResultFormats.mapStringFloatFormat

object GraphLod extends Controller {
  def getGraphStatistics(datasetId: String, groups: List[String]) = Action {
    val config = new Configuration()
    val db = new DatabaseConnection(config)
    val patternList: List[Pattern] = db.getPatterns(datasetId)
    val data: GraphLodResult = GraphLodResult(datasetId)
    val statistics = db.getStatistics(datasetId)

    data.nodes = db.getDatasetEntities(datasetId)
    data.edges = statistics.get("edges").getOrElse("0").toInt
    data.giantComponentEdges = statistics.get("gcnodes").getOrElse("0").toInt
    data.patterns = patternList
    data.connectedComponents = statistics.get("connectedcomponents").getOrElse("0").toInt
    data.stronglyConnectedComponents = statistics.get("stronglyconnectedcomponents").getOrElse("0").toInt

    statistics.get("nodedegreedistribution") match {
      case Some(ndd) => {
        val nodeDegreeDistributionMap = Json.parse(statistics.get("nodedegreedistribution").get).as[Map[Int, Int]]
        data.nodeDegreeDistribution =  nodeDegreeDistributionMap
      }
      case None => println()
    }
    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getGraphPatternStatistics(datasetId: String, groups: List[String], pattern: Int) = Action {
    val config = new Configuration()
    val db = new DatabaseConnection(config)
    val data: GraphLodResult = GraphLodResult(datasetId)
    val patternList: List[Pattern] = db.getColoredPatterns(datasetId, pattern)
    var entitiesPerClass: Map[String, Integer] = Map()
    var entities = 0
    if (groups.size > 0) {
      var newPatternList: List[Pattern] = Nil
      for (pattern : Pattern <- patternList) {
        var patternNotInGroups = false
        var newNodes: List[Node] = Nil
        var tempEntitiesPerClass: Map[String, Integer] = Map()
        for (node : Node <- pattern.nodes) {
          var newNode : Node = node
          var group = node.group.getOrElse("")
          if (!groups.contains(node.group.getOrElse(""))) {
            newNode = new Node(node.id, node.uri, None)
          } else {
            patternNotInGroups = true
          }
          if (group.length > 0) {
            var entityCount = 0
            if (tempEntitiesPerClass.contains(group)) {
              tempEntitiesPerClass.get(group) match {
                case Some(c) => entityCount = c
              }
            }
            entityCount += 1
            tempEntitiesPerClass += (group -> entityCount)
          }
          newNodes ::= newNode
        }
        if ((groups.size == 0) || ((groups.size > 0) && patternNotInGroups)) {
          newPatternList ::=new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links)
          entities += newNodes.size
          for ((group, count) <- tempEntitiesPerClass) {
            var entityCount = 0
            if (entitiesPerClass.contains(group)) {
              entitiesPerClass.get(group) match {
                case Some(c) => entityCount = c
              }
            }
            entityCount += count
            entitiesPerClass += (group -> entityCount)
          }
        }
      }
      data.patterns = newPatternList
    } else {
      data.patterns = patternList
    }

    if (entities > 0) {
      var classDistribution : Map[String, Double] = Map()
      for ((group, entityCount) <- entitiesPerClass) {
         classDistribution += (group -> (entityCount/entities).asInstanceOf[Double])
      }
      data.classDistribution =  classDistribution
    }

    val json = Json.obj("statistics" -> data)
    Ok(json)
  }

  def getBigComponent(dataset: String, groups: List[String], pattern: Int) = Action {
    Ok("this is big!")
  }
}
