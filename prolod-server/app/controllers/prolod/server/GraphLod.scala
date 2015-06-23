package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.GraphLodResultFormats.{graphLodResultFormat, mapIntIntFormat}
import prolod.common.models._

object GraphLod extends Controller {
  def getGraphStatistics(datasetId: String, groups: List[String]) = Action {
    val config = new Configuration()
    val db = new DatabaseConnection(config)
    val patternList: List[Pattern] = db.getPatterns(datasetId)
    val data: GraphLodResult = GraphLodResult(datasetId)
    val statistics = db.getStatistics(datasetId)

    data.nodes = db.getDatasetEntities(datasetId)
    data.edges = statistics.getOrElse("edges", "0").toInt
    data.giantComponentEdges = statistics.getOrElse("gcnodes", "0").toInt
    data.patterns = patternList
    data.connectedComponents = statistics.getOrElse("connectedcomponents", "0").toInt
    data.stronglyConnectedComponents = statistics.getOrElse("stronglyconnectedcomponents", "0").toInt

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
    var entitiesPerClass: Map[String, Int] = Map()
    var entities = 0
    if (groups.nonEmpty) {
      var newPatternList: List[Pattern] = Nil
      for (pattern : Pattern <- patternList) {
        var patternNotInGroups = false
        var newNodes: List[Node] = Nil
        var tempEntitiesPerClass: Map[String, Int] = Map()
        for (node : Node <- pattern.nodes) {
          var newNode : Node = node
          val group = node.group.getOrElse("")
          if (!groups.contains(node.group.getOrElse(""))) {
            newNode = new Node(node.id, node.uri, None)
          } else {
            patternNotInGroups = true
          }
          if (group.length > 0) {
            var entityCount = 0
            if (tempEntitiesPerClass.contains(group)) {
              entityCount = tempEntitiesPerClass.getOrElse(group, 0)
            }
            entityCount += 1
            tempEntitiesPerClass += (group -> entityCount)
          }
          newNodes ::= newNode
        }
        if (groups.isEmpty || (groups.nonEmpty && patternNotInGroups)) {
          newPatternList ::=new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links)
          entities += newNodes.size
          for ((group, count) <- tempEntitiesPerClass) {
            var entityCount = 0
            if (entitiesPerClass.contains(group)) {
              entityCount = entitiesPerClass.getOrElse(group, 0)
            }
            entityCount += count
            entitiesPerClass += (group -> entityCount)
          }
        }
      }
      data.patterns = newPatternList
    } else {
      data.patterns = patternList
      for (pattern : Pattern <- patternList) {
        var tempEntitiesPerClass: Map[String, Int] = Map()
        for (node : Node <- pattern.nodes) {
          val group = node.group.getOrElse("")
          if (group.length > 0) {
            var entityCount = 0
            if (tempEntitiesPerClass.contains(group)) {
              entityCount = tempEntitiesPerClass.getOrElse(group, 0)
            }
            entityCount += 1
            tempEntitiesPerClass += (group -> entityCount)
          }
        }
        entities += pattern.nodes.size
        for ((group, count) <- tempEntitiesPerClass) {
          var entityCount = 0
          if (entitiesPerClass.contains(group)) {
            entityCount = entitiesPerClass.getOrElse(group, 0)
          }
          entityCount += count
          entitiesPerClass += (group -> entityCount)
        }
      }
    }

    if (entities > 0) {
      var classDistribution : Map[String, Double] = Map()
      var entitiesUnknown = entities
      for ((group, entityCount) <- entitiesPerClass) {
         classDistribution += (group -> (entityCount.toDouble/entities))
        entitiesUnknown -= entityCount
      }
      if (entitiesUnknown > 0) {
        classDistribution += ("unknown" -> (entitiesUnknown.toDouble/entities))
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
