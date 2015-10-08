package prolod.common.models

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

case class PatternFromDB(val name: Option[String] = None,
                         val nodes: List[Node],
                         val links: List[Link])

case class Pattern(id: Int,
                   name: String,
                   occurences: Int,
                   nodes: List[Node],
                   links: List[Link],
                   diameter : Double = -1,
                   isoGroup: Option[Int] = None
                   )

case class Link(source: Int,
                uri: Option[String] = None,
                label: Option[String] = None,
                target: Int,
                surrounding: Option[Boolean] = None)

case class Node(id: Int,
                uri: Option[String] = None,
                label: Option[String] = None,
                group: Option[String] = None,
                surrounding: Option[Boolean] = None,
                dbId: Option[Int] = None)

object PatternFormats {
  implicit val linkFormat = Json.format[Link]
  implicit val nodeFormat = Json.format[Node]
  implicit val patternFormat = Json.format[Pattern]
  implicit val patternDBFormat = Json.format[PatternFromDB]
}
