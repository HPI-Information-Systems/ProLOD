package controllers

import model.ViewFormats._
import model._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object Suggestions extends Controller {

  def getSuggestions(dataset: String, group: String) = Action {
    val data: List[Suggestion] = List(Suggestion("a"))
    val json = Json.obj("data" -> data)
    Ok(json)
  }
}
