package prolod.common.models

import play.api.libs.json.{JsValue, JsObject, Json}

case class Pattern(json: JsValue,
                   occurences: Int) {

}

object PatternFormats {
  implicit val patternFormat = Json.format[Pattern]
}
