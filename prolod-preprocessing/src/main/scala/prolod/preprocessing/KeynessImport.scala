package prolod.preprocessing

import java.util

import de.hpi.fgis.loducc.Keyness
import prolod.common.config.DatabaseConnection
import scala.collection.JavaConverters._

class KeynessImport(var db: DatabaseConnection, var dataset: String) {

	def run: Unit = {
		val keyness : Keyness = new Keyness()
		db.insertKeyness(dataset, keyness)
	}
}
