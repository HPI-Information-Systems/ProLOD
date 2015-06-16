package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.Entity
import prolod.common.models.Triple
import prolod.common.models.EntityFormats.entityFormat

object Entity extends Controller {

	def getDetails(dataset: String, entity: String) = Action {
		var config = new Configuration()
		var db = new DatabaseConnection(config)
		val entityDetails: Entity = db.getEntityDetails(dataset, entity)
		val json = Json.obj("entity" -> entityDetails)
		Ok(json)
	}
}
