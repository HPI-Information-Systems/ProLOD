package controllers

import model.ViewFormats._
import model._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object AssociationRules extends Controller {

  def getAssociationRules(dataset: String, group: String) = Action {
    val data: List[AssociationRule] = List(AssociationRule("a", "b", 0.3, 0.9, 0.8))
    val json = Json.obj("data" -> data)
    Ok(json)
  }

}
