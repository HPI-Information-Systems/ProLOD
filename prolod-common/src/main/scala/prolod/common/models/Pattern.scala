package prolod.common.models

import play.api.libs.json.{JsValue, JsObject, Json}

case class Pattern(id: Int, name: String, occurences: Int, nodes: List[Node], links: List[Link])

case class Link(source: Int, target: Int)

case class Node(id: Int, group: Option[String] = None)

object PatternFormats {
  implicit val linkFormat = Json.format[Link]
  implicit val nodeFormat = Json.format[Node]
  implicit val patternFormat = Json.format[Pattern]
}
