package controllers.prolod.server

import prolod.common.config.{DatabaseConnection, Configuration}
import prolod.common.models.{Group, Dataset}
import prolod.common.models.DatasetFormats.datasetFormat
import play.api.mvc.{Action, Controller}
import play.api.libs.json._


object Datasets extends Controller {

	def datasets = Action {
		var config = new Configuration()
		var db = new DatabaseConnection(config)
		val data: List[Dataset] = db.getDatasets()
		val json = Json.obj("datasets" -> data)
		Ok(json)
	}
}
