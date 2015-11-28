package prolod.preprocessing

import java.io.FileInputStream
import java.net.{MalformedURLException, URL}
import graphlod.GraphLOD
import org.semanticweb.yars.nx.parser.NxParser
import org.semanticweb.yars.nx.Node
import prolod.common.config.{DatabaseConnection, Configuration}
import prolod.common.models.MaintableObject
import scala.collection.immutable.HashSet
import scala.io.Source
import scala.collection.JavaConverters._

class UpdateClusters(name : String, ontologyNamespace : String) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

    db.updateClasses(name, ontologyNamespace)
	db.updateClusterSizes(name, ontologyNamespace)
}

class UpdatePatterns(name : String) {
	var config = new Configuration()
	var db = new DatabaseConnection(config)

	db.updatePatterns(name)
}

class ImportDataset(name : String, namespace: String, ontologyNamespace : String, excludeNS : Option[String], files : Option[String], importTriplesFlag: Boolean, keyness: Boolean, addFiles: Boolean) {
    var config = new Configuration()
    var db = new DatabaseConnection(config)

    val datasetFiles: List[String] = if (files.isEmpty) Nil else List(files.get)
    val excludeNamespaces: List[String] = if (excludeNS.isEmpty) Nil else List(excludeNS.get)

    var subjectsKnown : Map[String, Int] = Map()

	if (addFiles) {
		importTriples(true)
        db.updateClasses(name, ontologyNamespace)
		db.updateClusterSizes(name, ontologyNamespace)
	} else {
		if (!keyness) {
			if (importTriplesFlag) {
				db.dropMainTables(name)
			}

			db.dropTables(name)
			db.createTables(name)

			if (importTriplesFlag) {
				importTriples(false)
			} else {
				subjectsKnown = db.getSubjectUris(name)
			}

			val graphlod = new GraphLodImport(db, name, namespace, ontologyNamespace, excludeNamespaces, datasetFiles, subjectsKnown)

			graphlod.run

			// db.updateClusterSizes(name, ontologyNamespace)

			//
			if (importTriplesFlag) {
				db.createIndices(name)
			}

			db.insertClusterSubjectTable(name, graphlod.graphLod.dataset)

			db.updateClusterSizes(name, ontologyNamespace)
			val keynessImporter = new KeynessImport(db, name)
			keynessImporter.run

		}  else {
			val keynessImporter = new KeynessImport(db, name)
			keynessImporter.run
		}
	}



    def importTriples(adding: Boolean) = {
        for (dataset <- datasetFiles) {
            var nxp: NxParser = null
            importTriplesByLine(new NxParser(new FileInputStream(dataset)), adding)
        }
    }

    def importTriplesByLine(nxp : NxParser, adding: Boolean): Unit = {
        var mtObjects : List[MaintableObject] = Nil

        var predicatesKnown : Map[String, Int] = Map()
        var objectsKnown : Map[String, Int] = Map()

        var subjectCount : Map[Int, Int] = Map()
        var predicateCount : Map[Int, Int] = Map()

        var externalLinks : List[Int] = Nil
        var internalLinks : Map[Int, Map[Int, String]] = Map()

	    if (addFiles) {
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
                    val sCount : Int = subjectCount.get(subjectId).getOrElse(0) + 1
                    subjectCount += (subjectId -> sCount)
                    val pCount : Int = predicateCount.get(predicateId).getOrElse(0) + 1
                    predicateCount += (predicateId -> pCount)

                    if (isValid(o)) {
                        if (o.startsWith(namespace) || o.startsWith(ontologyNamespace)) {
                            var internalLink : Map[Int, String] = Map(predicateId -> o)
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

                    if (addFiles) {
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

object ImportDataset {
    def main(args: Array[String]) {
	    if (args(0).equals("importTriples")) {
		    args match {
			    case Array(importFlag, name, namespace, ontologyNamespace, files)   => new ImportDataset(name, namespace, ontologyNamespace, None, Some(files), true, false, false)
			    case Array(importFlag, name, namespace, ontologyNamespace, excludeNS, files)
			    => new ImportDataset(name, namespace, ontologyNamespace, Some(excludeNS), Some(files), true, false, false)
			    case _                                                  => printUsage()
		    }
	    } else if (args(0).equals("updateClusterSizes")) {
		    args match {
			    case Array(updateClusterSizes, name, ontologyNamespace)   => new UpdateClusters(name, ontologyNamespace)
			    case _                                                    => printUsage()
		    }
	    } else if (args(0).equals("addImport")) {
			args match {
				case Array(add, name, namespace, ontologyNamespace, files) => new ImportDataset(name, namespace, ontologyNamespace, None, Some(files), false, false, true)
				case _ => printUsage()
			}
	    } else if (args(0).equals("updatePatternsWithIds")) {
		    args match {
			    case Array(updateClusterSizes, name)   => new UpdatePatterns(name)
			    case _                                                    => printUsage()
		    }
	    } else if (args(0).equals("keyness")) {
		    args match {
			    case Array(updateKeyness, name, namespace, ontologyNamespace, files)   => new ImportDataset(name, namespace, ontologyNamespace, None, Some(files), false, true, false)
			    case Array(updateKeyness, name, namespace, ontologyNamespace, excludeNS, files)
			    => new ImportDataset(name, namespace, ontologyNamespace, Some(excludeNS), Some(files), false, true, false)
			    case _                                                  => printUsage()
		    }
	    } else {
		    args match {
			    case Array(name, namespace, ontologyNamespace, files)   => new ImportDataset(name, namespace, ontologyNamespace, None, Some(files), false, false, false)
			    case Array(name, namespace, ontologyNamespace, excludeNS, files)
			    => new ImportDataset(name, namespace, ontologyNamespace, Some(excludeNS), Some(files), false, false, false)
			    case Array(name, namespace, ontologyNamespace, "-excludeNS", excludeNS, "-files", files)
			    => new ImportDataset(name, namespace, ontologyNamespace, Some(excludeNS), Some(files), false, false, false)
			    case _                                                  => printUsage()
		    }
	    }
    }

    private def printUsage() {
        println("usage:")
        println("  [importTriples|updateClusterSizes|addImport|keyness|updatePatternsWithIds] name namespace ontologyNamespace [files]")
        println("  name namespace ontologyNamespace [excludeNamespaces] [files]")
    }

}
