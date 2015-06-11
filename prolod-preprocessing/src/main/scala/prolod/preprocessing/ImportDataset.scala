package prolod.preprocessing

import prolod.common.config.{DatabaseConnection, Configuration}

class ImportDataset(name : String, namespace: String, ontologyNamespace : String, files : Option[String]) {
    var config = new Configuration()
    var db = new DatabaseConnection(config)

    val datasetFiles: List[String] = if (files.isEmpty) Nil else List(files.get)
    new GraphLodImport(db, name, namespace, ontologyNamespace, datasetFiles)


}

object ImportDataset {
    def main(args: Array[String]) {
        args match {
            case Array(name, namespace, ontologyNamespace, files)           => new ImportDataset(name, namespace, ontologyNamespace, Some(files))
            case _ => printUsage()
        }


    }

    private def printUsage() {
        println("usage:")
        println("  name namespace ontologyNamespace [files]")
        println("  name namespace ontologyNamespace -excludeNS [namespaces] -files [files]")
    }

}
