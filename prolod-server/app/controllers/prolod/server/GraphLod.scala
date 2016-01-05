package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.GraphLodResultFormats.{graphLodResultFormat, mapIntIntFormat}
import prolod.common.models._

object GraphLod extends Controller {
	def getGraphSimilarPattern(datasetId: String, groups: List[String], pattern: Int) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val patternList: List[Pattern] = db.getSimilarPattern(datasetId, None, pattern)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val statistics = db.getSimilarPatternStatistics(datasetId, pattern)

		data.patterns = patternList
		data.connectedComponents.count = statistics.getOrElse("patterns", "0").toInt

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getGraphSimilarPatterns(datasetId: String, groups: List[String]) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val patternList: List[Pattern] = db.getSimilarPatterns(datasetId, None)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val statistics = db.getStatistics(datasetId)

		//data.nodes = db.getDatasetEntities(datasetId)
		data.nodes = statistics.getOrElse("nodes", "0").toInt
		data.edges = statistics.getOrElse("edges", "0").toInt
		data.averageLinks = statistics.getOrElse("averagelinks", "0").toFloat
		data.giantComponent.nodes = statistics.getOrElse("gcnodes", "0").toInt
		data.giantComponent.edges = statistics.getOrElse("gcedges", "0").toInt
		data.patterns = patternList
		data.connectedComponents.count = statistics.getOrElse("connectedcomponents", "0").toInt
		data.stronglyConnectedComponents.count = statistics.getOrElse("stronglyconnectedcomponents", "0").toInt


		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getGraphStatistics(datasetId: String, groups: List[String]) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val patternList: List[Pattern] = db.getPatterns(datasetId, None)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val statistics = db.getStatistics(datasetId)

		//data.nodes = db.getDatasetEntities(datasetId)
		data.nodes = statistics.getOrElse("nodes", "0").toInt
		data.edges = statistics.getOrElse("edges", "0").toInt
		data.averageLinks = statistics.getOrElse("averagelinks", "0").toFloat
		data.giantComponent.nodes = statistics.getOrElse("gcnodes", "0").toInt
		data.giantComponent.edges = statistics.getOrElse("gcedges", "0").toInt
		data.patterns = patternList
		data.connectedComponents.count = statistics.getOrElse("connectedcomponents", "0").toInt
		data.stronglyConnectedComponents.count = statistics.getOrElse("stronglyconnectedcomponents", "0").toInt

		statistics.get("nodedegreedistribution") match {
			case Some(ndd) => {
				val nodeDegreeDistributionMap = Json.parse(statistics.get("nodedegreedistribution").get).as[Map[Int, Int]]
				data.nodeDegreeDistribution = nodeDegreeDistributionMap
			}
			case None => println()
		}

		statistics.get("highestIndegrees") match {
			case Some(ndd) => {
				val highestIndegreesMap = Json.parse(statistics.get("highestIndegrees").get).as[Map[String, Int]]
				var highestIndegreesCleanedMap: Map[String, Map[Int, Int]] = Map()
				for ((key, value) <- highestIndegreesMap) {
					var highestIndegreesInternalMap: Map[Int, Int] = Map()
					try {
						highestIndegreesInternalMap += (db.getSubjectId(datasetId, key) -> value)
						highestIndegreesCleanedMap += (key.replace(db.getNamespace(datasetId), datasetId + ":") -> highestIndegreesInternalMap)
					} catch {
						case e: NullPointerException => println(e.getMessage)
					}
				}
				data.highestIndegrees = highestIndegreesCleanedMap
			}
			case None => println()
		}

		statistics.get("highestOutdegrees") match {
			case Some(ndd) => {
				val highestOutdegreesMap = Json.parse(statistics.get("highestOutdegrees").get).as[Map[String, Int]]
				var highestOutdegreesCleanedMap: Map[String, Map[Int, Int]] = Map()
				for ((key, value) <- highestOutdegreesMap) {
					var highestOutdegreesInternalMap: Map[Int, Int] = Map()
					try {
						highestOutdegreesInternalMap += (db.getSubjectId(datasetId, key) -> value)
						highestOutdegreesCleanedMap += (key.replace(db.getNamespace(datasetId), datasetId + ":") -> highestOutdegreesInternalMap)
					} catch {
						case e: NullPointerException => println(e.getMessage)
					}
				}
				data.highestOutdegrees = highestOutdegreesCleanedMap
			}
			case None => println()
		}

		// TODO class distribution should go in here

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getGraphPatternStatistics(datasetId: String, groups: List[String], pattern: Int, coloredPattern: Int) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val patternList: List[Pattern] = db.getColoredPatterns(datasetId, pattern, coloredPattern, None)
		var entitiesPerClass: Map[String, Int] = Map()
		var nodesPerPattern = 0
		data.connectedComponents.count = patternList.size
		if (groups.nonEmpty) {
			var newPatternList: List[Pattern] = Nil
			for (pattern: Pattern <- patternList) {
				var patternNotInGroups = false
				var newNodes: List[Node] = Nil
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
					var newNode: Node = node
					val group = node.group.getOrElse("")
					if (!groups.contains(node.group.getOrElse(""))) {
						newNode = new Node(node.id, node.uri, None, dbId = node.dbId)
					} else {
						patternNotInGroups = true
					}
					if ((group.length > 0) && !(node.surrounding.getOrElse(false))) {
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
					newPatternList ::= new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links, -1, pattern.isoGroup)
					nodesPerPattern = newNodes.size
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
			data.connectedComponents.count = newPatternList.size
			data.patterns = newPatternList
		} else {
			data.patterns = patternList
			for (pattern: Pattern <- patternList) {
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
					val group = node.group.getOrElse("")
					if ((group.length > 0) && !(node.surrounding.getOrElse(false))) {
						var entityCount = 0
						if (tempEntitiesPerClass.contains(group)) {
							entityCount = tempEntitiesPerClass.getOrElse(group, 0)
						}
						entityCount += 1
						tempEntitiesPerClass += (group -> entityCount)
					}
				}
				nodesPerPattern = pattern.nodes.size
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

		if (nodesPerPattern > 0) {
			var classDistribution: Map[String, Double] = Map()
			var entitiesUnknown = nodesPerPattern
			for ((group, entityCount) <- entitiesPerClass) {
				classDistribution += (group -> (entityCount.toDouble / nodesPerPattern))
				entitiesUnknown -= entityCount
			}
			if (entitiesUnknown > 0) {
				classDistribution += ("unknown" -> (entitiesUnknown.toDouble / nodesPerPattern))
			}
			data.nodes = nodesPerPattern
			data.edges = data.patterns(0).links.size
			data.classDistribution = classDistribution
		}

		if (data.patterns.nonEmpty) {
			val patternDiameter = db.getPatternDiameter(datasetId, data.patterns.last.id)
			data.diameter = patternDiameter
		}

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getGCPatternStatistics(datasetId: String, groups: List[String], pattern: Int, coloredPattern: Int) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val patternList: List[Pattern] = db.getColoredPatterns(datasetId, pattern, coloredPattern, Some("_gc"))
		var entitiesPerClass: Map[String, Int] = Map()
		var nodes = 0
		var entities = 0
		data.connectedComponents.count = patternList.size
		if (groups.nonEmpty) {
			var newPatternList: List[Pattern] = Nil
			for (pattern: Pattern <- patternList) {
				var patternNotInGroups = false
				var newNodes: List[Node] = Nil
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
					var newNode: Node = node
					val group = node.group.getOrElse("")
					if (!groups.contains(node.group.getOrElse(""))) {
						newNode = new Node(node.id, node.uri, None, dbId = node.dbId)
					} else {
						patternNotInGroups = true
					}
					if ((group.length > 0) && !(node.surrounding.getOrElse(false))) {
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
					newPatternList ::= new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links, -1, pattern.isoGroup)
					nodes = newNodes.size
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
			data.connectedComponents.count = newPatternList.size
			data.patterns = newPatternList
		} else {
			data.patterns = patternList
			for (pattern: Pattern <- patternList) {
				if (nodes == 0) {
					// data.name = pattern.name
					val level1Patterns: List[Node] = pattern.nodes.filter(_.surrounding.getOrElse(false) == false)
					nodes = level1Patterns.size
					var tempEntitiesPerClass: Map[String, Int] = Map()
					for (node: Node <- pattern.nodes) {
						val group = node.group.getOrElse("")
						if ((group.length > 0) && !(node.surrounding.getOrElse(false))) {
							var entityCount = 0
							if (tempEntitiesPerClass.contains(group)) {
								entityCount = tempEntitiesPerClass.getOrElse(group, 0)
							}
							entityCount += 1
							tempEntitiesPerClass += (group -> entityCount)
						}
					}
					entities = nodes
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
		}

		if (entities > 0) {
			var classDistribution: Map[String, Double] = Map()
			var entitiesUnknown = entities
			for ((group, entityCount) <- entitiesPerClass) {
				classDistribution += (group -> (entityCount.toDouble / entities))
				entitiesUnknown -= entityCount
			}
			if (entitiesUnknown > 0) {
				classDistribution += ("unknown" -> (entitiesUnknown.toDouble / entities))
			}
			data.nodes = nodes
			data.classDistribution = classDistribution
		}

		if (data.patterns.nonEmpty) {
			val patternDiameter = db.getPatternDiameter(datasetId, data.patterns.last.id)
			data.diameter = patternDiameter

			//data.patternTypes = db.getPatternTypes(datasetId, data.patterns.last.id, data.patterns.last.isoGroup, Some("_gc"))

		}

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getGCIsoPatternStatistics(datasetId: String, groups: List[String], pattern: Int) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val patternList: List[Pattern] = db.getColoredIsoPatterns(datasetId, pattern, Some("_gc"))
		var entitiesPerClass: Map[String, Int] = Map()
		var entities = 0
		data.connectedComponents.count = patternList.size
		if (groups.nonEmpty) {
			var newPatternList: List[Pattern] = Nil
			for (pattern: Pattern <- patternList) {
				var patternNotInGroups = false
				var newNodes: List[Node] = Nil
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
					var newNode: Node = node
					val group = node.group.getOrElse("")
					if (!groups.contains(node.group.getOrElse(""))) {
						newNode = new Node(node.id, node.uri, None, dbId = node.dbId)
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
					newPatternList ::= new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links, -1, pattern.isoGroup)
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
			data.connectedComponents.count = newPatternList.size
			data.patterns = newPatternList
		} else {
			data.patterns = patternList
			for (pattern: Pattern <- patternList) {
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
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
			var classDistribution: Map[String, Double] = Map()
			var entitiesUnknown = entities
			for ((group, entityCount) <- entitiesPerClass) {
				classDistribution += (group -> (entityCount.toDouble / entities))
				entitiesUnknown -= entityCount
			}
			if (entitiesUnknown > 0) {
				classDistribution += ("unknown" -> (entitiesUnknown.toDouble / entities))
			}
			data.nodes = entities
			data.classDistribution = classDistribution
		}

		if (data.patterns.nonEmpty) {
			val patternDiameter = db.getPatternDiameter(datasetId, data.patterns.last.id)
			data.diameter = patternDiameter
		}

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getGraphIsoPatternStatistics(datasetId: String, groups: List[String], pattern: Int) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val patternList: List[Pattern] = db.getColoredIsoPatterns(datasetId, pattern, None)
		var entitiesPerClass: Map[String, Int] = Map()
		var entities = 0
		data.connectedComponents.count = patternList.size
		if (groups.nonEmpty) {
			var newPatternList: List[Pattern] = Nil
			for (pattern: Pattern <- patternList) {
				var patternNotInGroups = false
				var newNodes: List[Node] = Nil
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
					var newNode: Node = node
					val group = node.group.getOrElse("")
					if (!groups.contains(node.group.getOrElse(""))) {
						newNode = new Node(node.id, node.uri, None, dbId = node.dbId)
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
					newPatternList ::= new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links)
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
			data.connectedComponents.count = newPatternList.size
			data.patterns = newPatternList
		} else {
			data.patterns = patternList
			for (pattern: Pattern <- patternList) {
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
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
			var classDistribution: Map[String, Double] = Map()
			var entitiesUnknown = entities
			for ((group, entityCount) <- entitiesPerClass) {
				classDistribution += (group -> (entityCount.toDouble / entities))
				entitiesUnknown -= entityCount
			}
			if (entitiesUnknown > 0) {
				classDistribution += ("unknown" -> (entitiesUnknown.toDouble / entities))
			}
			data.nodes = data.patterns(0).nodes.size
			data.edges = data.patterns(0).links.size
			data.classDistribution = classDistribution
		}

		if (data.patterns.nonEmpty) {
			val patternDiameter = db.getPatternDiameter(datasetId, data.patterns.last.id)
			data.diameter = patternDiameter
		}

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}

	def getBigComponent(datasetId: String, groups: List[String]) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: GraphLodResult = GraphLodResult(datasetId)
		val patternList: List[Pattern] = db.getPatterns(datasetId, Some("_gc"))
		var entitiesPerClass: Map[String, Int] = Map()
		var entities = 0
		data.connectedComponents.count = patternList.size
		if (groups.nonEmpty) {
			var newPatternList: List[Pattern] = Nil
			for (pattern: Pattern <- patternList) {
				var patternNotInGroups = false
				var newNodes: List[Node] = Nil
				var tempEntitiesPerClass: Map[String, Int] = Map()
				for (node: Node <- pattern.nodes) {
					if (!node.surrounding.getOrElse(false)) {
						var newNode: Node = node
						val group = node.group.getOrElse("")
						if (!groups.contains(node.group.getOrElse(""))) {
							newNode = new Node(node.id, node.uri, None, dbId = node.dbId)
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
				}
				if (groups.isEmpty || (groups.nonEmpty && patternNotInGroups)) {
					newPatternList ::= new Pattern(pattern.id, pattern.name, pattern.occurences, newNodes, pattern.links)
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
			data.connectedComponents.count = newPatternList.size
			data.patterns = newPatternList
		} else {
			data.patterns = patternList
			for (pattern: Pattern <- patternList) {
				var tempEntitiesPerClass: Map[String, Int] = Map()
				var entityCount: Int = 0
				for (node: Node <- pattern.nodes) {
					if (!node.surrounding.getOrElse(false)) {
						val group = node.group.getOrElse("")
						if (group.length > 0) {
							var entityCount = 0
							if (tempEntitiesPerClass.contains(group)) {
								entityCount = tempEntitiesPerClass.getOrElse(group, 0)
							}
							entityCount += 1
							tempEntitiesPerClass += (group -> entityCount)
						}
						entityCount += 1
					}
				}
				entities += entityCount
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
			var classDistribution: Map[String, Double] = Map()
			var entitiesUnknown = entities
			for ((group, entityCount) <- entitiesPerClass) {
				classDistribution += (group -> (entityCount.toDouble / entities))
				entitiesUnknown -= entityCount
			}
			if (entitiesUnknown > 0) {
				classDistribution += ("unknown" -> (entitiesUnknown.toDouble / entities))
			}
			//data.nodes = entities
			data.classDistribution = classDistribution
		}

		if (data.patterns.nonEmpty) {
			val patternDiameter = db.getPatternDiameter(datasetId, data.patterns.last.id)
			//data.diameter = patternDiameter
		}

		val json = Json.obj("statistics" -> data)
		Ok(json)
	}
}