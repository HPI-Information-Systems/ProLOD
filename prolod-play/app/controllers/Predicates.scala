package controllers

import models.ViewFormats._
import models._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object Predicates extends Controller {

  def getPredicates(dataset: String, group: List[String]) = Action {
    val data: List[Predicate] = Nil // List(Predicate("a", 10, 0.3))
    val json = Json.obj("data" -> data)
    Ok(json)
  }

}
