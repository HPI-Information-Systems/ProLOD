package controllers.prolod.server

import model.prolod.server.DatasetFormats._
import model.prolod.server.Dataset
import model.prolod.server.Group
import play.api.mvc.{Action, Controller}
import play.api.libs.json._


object Datasets extends Controller {

	val list = List(
		Dataset(
			"DBpedia",
			10,
			List(
				Group("humans", 7),
				Group("cars", 3))
		),
		Dataset(
			"Drugbank",
			5,
			List(
				Group("Drugs", 3),
				Group("Diseases", 2)
			)
		)
	)

	def datasets = Action {
		val data: List[Dataset] = list
		val json = Json.obj("datasets" -> data)
		Ok(json)
	}
}
