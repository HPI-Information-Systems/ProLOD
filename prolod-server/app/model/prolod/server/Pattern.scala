package model.prolod.server

import play.api.libs.json.{Json, Writes}

import java.util.HashMap

case class Pattern(json : String,
	                      occurences : Int) {

}

object PatternFormats {
	implicit val patternFormat = Json.format[Pattern]
}
