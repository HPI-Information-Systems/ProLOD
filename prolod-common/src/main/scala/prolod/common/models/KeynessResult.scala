package prolod.common.models

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

case class KeynessResult(property: String, keyness: Double, uniqueness: Double, density: Double, values: Int, cluster: String)

object KeynessResultFormats {
	implicit val keynessResultFormat = Json.format[KeynessResult]
}