package prolod.common.models

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
/*
case class KeynessResult(datasetId: String,
                         keyness: List[Keyness] = List()
                             ) {


}
  */
case class KeynessResult(property: String, keyness: Double, uniqueness: Double, density: Double, values: Int)

/*
object KeynessResultLoader {
	def load(datasetId: String) = {
		new KeynessResult(datasetId)
	}
}
  */
object KeynessResultFormats {
	//implicit val keynessFormat = Json.format[Keyness]
	implicit val keynessResultFormat = Json.format[KeynessResult]
}