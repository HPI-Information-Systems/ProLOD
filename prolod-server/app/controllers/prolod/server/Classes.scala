package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.ClassHierarchy
import prolod.common.models.ClassHierarchyFormats.classHierarchyFormat

import scala.collection.mutable

object Classes extends Controller {

	def classes(dataset: String) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val data = db.getClassHierarchy(dataset)
		val seenClasses = new mutable.HashSet[String]()
		val hierarchy = buildHierarchy("http://www.w3.org/2002/07/owl#Thing", data, seenClasses)
		val json = Json.obj("classes" -> hierarchy)
		Ok(json)
	}

	def buildHierarchy(className: String, data: Map[String, Seq[String]], seenClasses: mutable.Set[String]): ClassHierarchy = {
		if(seenClasses.contains(className)) {
			return null // return null when a circle or multiple inheritance is found 
		}
		seenClasses.add(className)
		val children = data.getOrElse(className, Seq()).map(child => buildHierarchy(child, data, seenClasses)).filter(p => p!=null)
		val ownSize = 1 // TODO calculate size
		val totalSize = children.map(c => c.size).sum + ownSize
		new ClassHierarchy(className, totalSize, children)
	}
}
