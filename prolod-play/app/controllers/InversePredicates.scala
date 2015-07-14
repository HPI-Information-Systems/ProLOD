package controllers

import models.InversePredicate
import models.ViewFormats._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object InversePredicates extends Controller {

  def getInversePredicates(dataset: String, group: List[String]) = Action {
    val data: List[InversePredicate] = Nil // List(InversePredicate("a", "b", 0.3, 0.5))
    val json = Json.obj("data" -> data)
    Ok(json)
  }

}
