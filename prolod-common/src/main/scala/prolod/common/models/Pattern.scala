package prolod.common.models

import play.api.libs.json.Json

case class Pattern(json : String,
	                      occurences : Int) {

}

object PatternFormats {
	implicit val patternFormat = Json.format[Pattern]
}
