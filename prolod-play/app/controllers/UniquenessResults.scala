package controllers

import model.ViewFormats._
import model._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object UniquenessResults extends Controller {

  def getUniqueness(dataset: String, group: String) = Action {
    val data: List[Uniqueness] = List(Uniqueness("a", 0.3, 0.2, 0.1, 10, 2))
    val json = Json.obj("data" -> data)
    Ok(json)
  }
}
