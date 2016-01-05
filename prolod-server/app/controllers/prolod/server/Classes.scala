package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.{Group, ClassHierarchy}
import prolod.common.models.ClassHierarchyFormats.classHierarchyFormat

import scala.collection.mutable

object Classes extends Controller {

	def classes(dataset: String) = Action {
		val config = new Configuration()
		val db = new DatabaseConnection(config)
		val ns = db.getOntologyNamespace(dataset)
		val data = db.getClassHierarchy(dataset, ns)
		val groups = db.getClusters(dataset, ns)
		val seenClasses = new mutable.HashSet[String]()
		val sizes = buildSizes(groups)
		val hierarchy = buildHierarchy("http://www.w3.org/2002/07/owl#Thing", data, sizes, seenClasses)
		val json = Json.obj("classes" -> hierarchy)
		Ok(json)
	}

	def buildHierarchy(className: String, data: Map[String, Seq[String]], sizes: Map[String, Int], seenClasses: mutable.Set[String]): ClassHierarchy = {
		if(seenClasses.contains(className)) {
			return null // return null when a circle or multiple inheritance is found 
		}
		seenClasses.add(className)
		val children = data.getOrElse(className, Seq()).map(child => buildHierarchy(child, data, sizes, seenClasses)).filter(p => p!=null)
		val ownSize = sizes(className)
		val totalSize = ownSize + children.map(c => c.size).sum
		new ClassHierarchy(className, totalSize, children)
	}

	def buildSizes(groups: Seq[Group]): Map[String, Int] = {
		groups.map(group => (group.name, group.size)).toMap
	}
}
