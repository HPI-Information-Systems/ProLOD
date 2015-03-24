package controllers

import model.DatasetFormats._
import model.{Dataset, Group}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}


object Datasets extends Controller {

  val list = List(
    Dataset(
      "DBpedia",
      List(
        Group("humans"),
        Group("cars"))
    ),
    Dataset(
      "Drugbank",
      List(
        Group("Drugs"),
        Group("Diseases")
      )
    )
  )

  def datasets = Action {
    val data: List[Dataset] = list
    val json = Json.obj("data" -> data)
    Ok(json)
  }

}
