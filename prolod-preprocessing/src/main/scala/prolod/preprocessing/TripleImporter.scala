package prolod.preprocessing
import java.io.FileInputStream

import org.semanticweb.yars.nx.parser.NxParser
import prolod.common.config.DatabaseConnection

trait TripleImporter {
	def importTriples(db: DatabaseConnection, adding: Boolean)
}
