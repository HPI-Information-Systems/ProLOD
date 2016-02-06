package prolod.preprocessing

import java.io.FileInputStream
import java.net.{MalformedURLException, URL}

import org.semanticweb.yars.nx.Node
import org.semanticweb.yars.nx.parser.NxParser
import prolod.common.config.DatabaseConnection
import prolod.common.models.MaintableObject

class TripleImporter(db: DatabaseConnection, name: String, namespace: String, ontologyNamespace: String, datasetFiles: List[String]) {

	def importTriples(adding: Boolean) = {
		for (dataset <- datasetFiles) {
			var nxp: NxParser = null
			importTriplesByLine(new NxParser(new FileInputStream(dataset)), adding)
		}
	}

	def importTriplesByLine(nxp: NxParser, adding: Boolean): Unit = {
		var mtObjects: List[MaintableObject] = Nil

		var predicatesKnown: Map[String, Int] = Map()
		var objectsKnown: Map[String, Int] = Map()

		var subjectCount: Map[Int, Int] = Map()
		var predicateCount: Map[Int, Int] = Map()

		var externalLinks: List[Int] = Nil
		var internalLinks: Map[Int, Map[Int, String]] = Map()

		var subjectsKnown: Map[String, Int] = Map()

		if (adding) {
			subjectsKnown = db.getSubjectUris(name)
			predicatesKnown = db.getPredicateUris(name)
			objectsKnown = db.getObjectUris(name)
		}

		while (nxp.hasNext) {
			var subjectId = -1
			var objectId = -1
			var predicateId = -1

			val nodes: Array[Node] = nxp.next
			if (nodes.size >= 3) {
				val s: String = nodes(0).toString
				val p: String = nodes(1).toString
				val o: String = nodes(2).toString
				if (!subjectsKnown.contains(s)) {
					subjectId = db.insertSubject(name, s)
					subjectsKnown += (s -> subjectId)
				} else {
					subjectId = subjectsKnown.get(s).get
				}
				if (!predicatesKnown.contains(p)) {
					predicateId = db.insertPredicate(name, p)
					predicatesKnown += (p -> predicateId)
				} else {
					predicateId = predicatesKnown.get(p).get
				}
				if (!objectsKnown.contains(o)) {
					objectId = db.insertObject(name, o)
					objectsKnown += (o -> objectId)
				} else {
					objectId = objectsKnown.get(o).get
				}
				if (subjectId >= 0 && predicateId >= 0 && objectId >= 0) {
					val sCount: Int = subjectCount.get(subjectId).getOrElse(0) + 1
					subjectCount += (subjectId -> sCount)
					val pCount: Int = predicateCount.get(predicateId).getOrElse(0) + 1
					predicateCount += (predicateId -> pCount)

					if (isValid(o)) {
						if (o.startsWith(namespace) || o.startsWith(ontologyNamespace)) {
							var internalLink: Map[Int, String] = Map(predicateId -> o)
							if (internalLinks.contains(subjectId)) {
								internalLink.get(subjectId)
							}
							internalLinks += (subjectId -> internalLink)
						} else {
							externalLinks ::= objectId
						}
					}

					// TODO datatype
					// TODO pattern_id
					// TODO normalizedpattern_id
					// TODO parsed_value

					// TODO load prefix.cc and count external links to external datasets!
					// exclude vocabulary prefixes by excluding properties that define schemata

					// TODO save first, add later when internal links can we looked up

					//mtObjects ::=
					val mtObject: MaintableObject = new MaintableObject(subjectId, predicateId, objectId)
					if (internalLinks.contains(mtObject.subjectId)) {
						if (internalLinks.get(mtObject.subjectId).contains(mtObject.propertyId)) {
							mtObject.internalLink = subjectsKnown.get(internalLinks.get(mtObject.subjectId).get(mtObject.propertyId))
						}
					}

					if (adding) {
						db.insertTriplesIfNotYet(name, mtObject)
					} else {
						db.insertTriples(name, mtObject)
					}

				}
			}
		}

		// TODO final internal link check
		/*
		for (mtObject <- mtObjects) {
				if (internalLinks.contains(mtObject.subjectId)) {
						if (internalLinks.get(mtObject.subjectId).contains(mtObject.propertyId)) {
								mtObject.internalLink = subjectsKnown.get(internalLinks.get(mtObject.subjectId).get(mtObject.propertyId))
						}
				}

			if (addFiles) {
				db.insertTriplesIfNotYet(name, mtObject)
			} else {
				db.insertTriples(name, mtObject)
			}
		}
		*/
		db.updateSubjectCounts(name, subjectCount)
		db.updatePropertyCounts(name, predicateCount)
	}

	private def isValid(url: String): Boolean = {
		try {
			val checked: URL = new URL(url)
			return true
		} catch {
			case e: MalformedURLException => {
				return false
			}
		}
	}
}
