package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.Dataset
import prolod.common.models.DatasetFormats.datasetFormat

object Datasets extends Controller {

	def datasets = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: Seq[Dataset] = db.getDatasets()
		val json = Json.obj("datasets" -> data)
		Ok(json)
	}
}
