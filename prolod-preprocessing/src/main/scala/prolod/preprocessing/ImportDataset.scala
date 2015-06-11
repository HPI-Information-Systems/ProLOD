package prolod.preprocessing

import prolod.common.config.{DatabaseConnection, Configuration}

class ImportDataset(name : String, namespace: String, ontologyNamespace : String, excludeNS : Option[String], files : Option[String]) {
    var config = new Configuration()
    var db = new DatabaseConnection(config)

    val datasetFiles: List[String] = if (files.isEmpty) Nil else List(files.get)
    val excludeNamespaces: List[String] = if (excludeNS.isEmpty) Nil else List(excludeNS.get)
    new GraphLodImport(db, name, namespace, ontologyNamespace, excludeNamespaces, datasetFiles)


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
        println("  name namespace ontologyNamespace -excludeNS [namespaces] -files [files]")
    }

}
