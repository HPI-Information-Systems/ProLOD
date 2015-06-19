package prolod.preprocessing

import java.io.FileInputStream
import java.sql.SQLSyntaxErrorException
import org.semanticweb.yars.nx.parser.NxParser
import org.semanticweb.yars.nx.Node
import prolod.common.config.{DatabaseConnection, Configuration}
import scala.io.Source

class ImportDataset(name : String, namespace: String, ontologyNamespace : String, excludeNS : Option[String], files : Option[String]) {
    var config = new Configuration()
    var db = new DatabaseConnection(config)

    val datasetFiles: List[String] = if (files.isEmpty) Nil else List(files.get)
    val excludeNamespaces: List[String] = if (excludeNS.isEmpty) Nil else List(excludeNS.get)

    db.createTables(name)
    new GraphLodImport(db, name, namespace, ontologyNamespace, excludeNamespaces, datasetFiles)

    importTriples()

    def importTriples() = {
        for (dataset <- datasetFiles) {
            var nxp: NxParser = null
            importTriplesByLine(new NxParser(new FileInputStream(dataset)))
        }
    }

    def importTriplesByLine(nxp : NxParser): Unit = {
        var subjectsKnown : List[String] = Nil
        var predicatesKnown : List[String] = Nil
        var objectsKnown : List[String] = Nil

        while (nxp.hasNext) {
            var subjectId = -1
            var objectId = -1
            var predicateId = -1

            val nodes: Array[Node] = nxp.next
            val s: String = nodes(0).toString
            val p: String = nodes(1).toString
            val o: String = nodes(2).toString
            if (!subjectsKnown.contains(s)) {
                subjectId = db.insertSubject(name, s)
                subjectsKnown ::= s
            } else {
                subjectId = db.getSubjectId(name, s)
            }
            if (!predicatesKnown.contains(p)) {
                predicateId = db.insertPredicate(name, p)
                predicatesKnown ::= p
            } else {
                predicateId = db.getPredicateId(name, p)
            }
            if (!objectsKnown.contains(o)) {
                objectId = db.insertObject(name, o)
                objectsKnown ::= o
            } else {
                objectId = db.getObjectId(name, o)
            }
            if (subjectId >= 0 && predicateId >= 0 && objectId >= 0) {
                db.insertTriples(name, subjectId, predicateId, objectId)
            }
        }
    }





}

object ImportDataset {
    def main(args: Array[String]) {
        args match {
            case Array(name, namespace, ontologyNamespace, files)   => new ImportDataset(name, namespace, ontologyNamespace, None, Some(files))
            case Array(name, namespace, ontologyNamespace, excludeNS, files)
                                                                    => new ImportDataset(name, namespace, ontologyNamespace, Some(excludeNS), Some(files))
            case Array(name, namespace, ontologyNamespace, "-excludeNS", excludeNS, "-files", files)
                                                                    => new ImportDataset(name, namespace, ontologyNamespace, Some(excludeNS), Some(files))
            case _                                                  => printUsage()
        }


    }

    private def printUsage() {
        println("usage:")
        println("  name namespace ontologyNamespace [files]")
        println("  name namespace ontologyNamespace [excludeNamespaces] [files]")
    }

}
