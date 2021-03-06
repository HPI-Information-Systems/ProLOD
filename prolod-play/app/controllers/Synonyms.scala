package controllers

import models.ViewFormats._
import models._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object Synonyms extends Controller {

  def getSynonyms(dataset: String, group: List[String]) = Action {
    val data: List[Synonym] = Nil // List(Synonym("a", "b", 0.3, 0.8))
    val json = Json.obj("data" -> data)
    Ok(json)
  }
}
