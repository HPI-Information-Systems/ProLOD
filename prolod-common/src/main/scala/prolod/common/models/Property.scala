package prolod.common.models

import play.api.libs.json.Json

case class Property(id : Int, url: String, occurences: Int, percentage: Float)

object PropertyFormats{
	implicit val propertyFormat = Json.format[Property]
}



