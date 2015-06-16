package prolod.common.models

import play.api.libs.json.Json

case class Entity(url: String, label: String, triples: List[Triple])

case class Triple(s: String, p: String, o: String)

object EntityFormats{
  implicit val tripleFormat = Json.format[Triple]
  implicit val entityFormat = Json.format[Entity]
}