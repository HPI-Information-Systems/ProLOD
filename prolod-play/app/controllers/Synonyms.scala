package controllers

import model.ViewFormats._
import model._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object Synonyms extends Controller {

  def getSynonyms(dataset: String, group: String) = Action {
    val data: List[Synonym] = List(Synonym("a", "b", 0.3, 0.8))
    val json = Json.obj("data" -> data)
    Ok(json)
  }
}
