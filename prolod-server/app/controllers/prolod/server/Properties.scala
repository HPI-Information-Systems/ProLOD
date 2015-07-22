package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.Property
import prolod.common.models.PropertyFormats.propertyFormat

object Properties extends Controller {

	def getPropertyStatistics(dataset: String, groups: List[String]) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data: Seq[Property] =  db.getPropertyStatistics(dataset, groups)
		val json = Json.obj("data" -> data)
		Ok(json)
	}
}
