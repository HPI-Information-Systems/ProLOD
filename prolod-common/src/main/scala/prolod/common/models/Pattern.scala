package prolod.common.models

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

case class PatternFromDB(val nodes: List[Node], val links: List[Link])

case class Pattern(id: Int, name: String,
                   occurences: Int,
                   nodes: List[Node],
                   links: List[Link],
                   diameter : Double = -1
                   )

case class Link(source: Int, uri: Option[String] = None, label: Option[String] = None, target: Int)

case class Node(id: Int, uri: Option[String] = None, label: Option[String] = None, group: Option[String] = None)

object PatternFormats {
  implicit val linkFormat = Json.format[Link]
  implicit val nodeFormat = Json.format[Node]
  implicit val patternFormat = Json.format[Pattern]
  implicit val patternDBFormat = Json.format[PatternFromDB]
}
