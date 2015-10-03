package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models
import prolod.common.models.EntityFormats.entityFormat

object Entity extends Controller {

	def getDetails(dataset: String, entity: Int) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val entityDetails: models.Entity = db.getEntityDetails(dataset, entity)
		val json = Json.obj("entity" -> entityDetails)
		Ok(json)
	}

	def getDetails(dataset: String, entity: String) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val entityDetails: models.Entity = db.getEntityDetails(dataset, entity)
		val json = Json.obj("entity" -> entityDetails)
		Ok(json)
	}
}
