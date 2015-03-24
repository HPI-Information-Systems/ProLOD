package controllers

import model.{Dataset, Group}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}


object Datasets extends Controller {

  implicit val groupFormat = Json.format[Group]
  implicit val databaseSourceFormat = Json.format[Dataset]

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
    val json = Json.obj("data" -> list)
    Ok(json)
  }
}
