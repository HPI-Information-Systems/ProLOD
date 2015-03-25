package controllers

import model.DatasetFormats._
import model.{Dataset, Group}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}


object Datasets extends Controller {

  val list = List(
    Dataset(
      "DBpedia",
      10,
      List(
        Group("humans", 7),
        Group("cars", 3))
    ),
    Dataset(
      "Drugbank",
      5,
      List(
        Group("Drugs", 3),
        Group("Diseases", 2)
      )
    )
  )

  def datasets = Action {
    val data: List[Dataset] = list
    val json = Json.obj("datasets" -> data)
    Ok(json)
  }

}
