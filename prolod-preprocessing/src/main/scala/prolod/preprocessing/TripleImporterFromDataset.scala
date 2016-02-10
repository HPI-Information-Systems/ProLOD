package prolod.preprocessing

import graphlod.dataset.Dataset
import prolod.common.config.DatabaseConnection
import prolod.common.models.MaintableObject

import scala.collection.JavaConversions.asScalaSet

class TripleImporterFromDataset(name: String, namespace: String, ontologyNamespace: String, dataset: Dataset) extends TripleImporter {

	var mtObjects: List[MaintableObject] = Nil

	var predicatesKnown: Map[String, Int] = Map()
	var objectsKnown: Map[String, Int] = Map()

	var subjectCount: Map[Int, Int] = Map()
	var predicateCount: Map[Int, Int] = Map()

	var externalLinks: List[Int] = Nil
	var internalLinks: Map[Int, Map[Int, String]] = Map()

	var subjectsKnown: Map[String, Int] = Map()

	val RDF_TYPE: String = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"

	@Override
	def importTriples(db: DatabaseConnection, adding: Boolean) = {

		if (adding) {
			subjectsKnown = db.getSubjectUris(name)
			predicatesKnown = db.getPredicateUris(name)
			objectsKnown = db.getObjectUris(name)
		}

		val edgeSet = dataset.getGraph.edgeSet()

		var i = 0
		println("inserting " + edgeSet.size() + " triples")
		for (edge <- edgeSet) {
			i += 1
			if (i % 100 == 0) {
				println("inserting triples: " + i + "/" + edgeSet.size())
			}

			val s: String = edge.getSource.toString
			val p: String = edge.getUserObject.toString
			val o: String = edge.getTarget.toString

			addTriple(db, s, p, o, adding)
		}

		i = 0
		println("inserting classes triples")
		for (entry <- dataset.getClasses.entrySet()) {
			i += 1
			if (i % 100 == 0) {
				println("inserting classes: " + i + "/" + dataset.getClasses.size())
			}
			addTriple(db, entry.getKey, RDF_TYPE, entry.getValue, adding)
		}

		db.updateSubjectCounts(name, subjectCount)
		db.updatePropertyCounts(name, predicateCount)
	}

	def addTriple(db: DatabaseConnection, s: String, p: String, o: String, adding: Boolean) = {
		var subjectId = -1
		var objectId = -1
		var predicateId = -1

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
			val sCount: Int = subjectCount.getOrElse(subjectId, 0) + 1
			subjectCount += (subjectId -> sCount)
			val pCount: Int = predicateCount.getOrElse(predicateId, 0) + 1
			predicateCount += (predicateId -> pCount)

			if (o.startsWith(namespace) || o.startsWith(ontologyNamespace)) {
				val internalLink: Map[Int, String] = Map(predicateId -> o)
				if (internalLinks.contains(subjectId)) {
					internalLink.get(subjectId)
				}
				internalLinks += (subjectId -> internalLink)
			} else {
				externalLinks ::= objectId
			}

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
