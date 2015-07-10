package controllers

import models.ViewFormats._
import models._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object Suggestions extends Controller {

  def getSuggestions(dataset: String, group: List[String]) = Action {
    val data: List[Suggestion] = List(Suggestion("a"))
    val json = Json.obj("data" -> data)
    Ok(json)
  }
}
