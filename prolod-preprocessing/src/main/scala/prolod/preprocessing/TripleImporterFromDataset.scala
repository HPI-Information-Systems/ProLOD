package prolod.preprocessing

import graphlod.dataset.Dataset
import org.jgraph.graph.DefaultEdge
import prolod.common.config.DatabaseConnection
import prolod.common.models.MaintableObject

import scala.collection.JavaConversions.asScalaSet

class TripleImporterFromDataset(name: String, namespace: String, ontologyNamespace: String, dataset: Dataset) extends TripleImporter{

	@Override
	def importTriples(db: DatabaseConnection, adding: Boolean) = {

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

		val edgeSet = dataset.getGraph.edgeSet()

		for (edge <- edgeSet) {
			var subjectId = -1
			var objectId = -1
			var predicateId = -1

			val s: String = edge.getSource.toString
			val p: String = edge.getUserObject.toString
			val o: String = edge.getTarget.toString

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

		db.updateSubjectCounts(name, subjectCount)
		db.updatePropertyCounts(name, predicateCount)
	}

}
