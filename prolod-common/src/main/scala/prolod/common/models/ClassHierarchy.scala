package prolod.common.models

import play.api.libs.json.Json

case class ClassHierarchy(name: String, size: Int, children: Seq[ClassHierarchy])

object ClassHierarchyFormats {
	implicit val classHierarchyFormat = Json.format[ClassHierarchy]
}
