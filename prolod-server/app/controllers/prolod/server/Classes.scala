package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.ClassHierarchy
import prolod.common.models.ClassHierarchyFormats.classHierarchyFormat

object Classes extends Controller {

	def classes(dataset: String) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data = db.getClassHierarchy(dataset)
		val hierarchy = buildHierarchy("http://www.w3.org/2002/07/owl#Thing", data)
		val json = Json.obj("classes" -> hierarchy)
		Ok(json)
	}

	def buildHierarchy(className: String, data: Map[String, Seq[String]]): ClassHierarchy = {
		val children = data.getOrElse(className, Seq()).map(child => buildHierarchy(child, data))
		val size = children.map(c => c.size).sum + 1
		new ClassHierarchy(className, size, children)
	}
}
