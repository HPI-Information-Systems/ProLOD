package controllers.prolod.server

import prolod.common.models.{Group, Dataset}
import prolod.common.models.DatasetFormats.datasetFormat
import play.api.mvc.{Action, Controller}
import play.api.libs.json._


object Datasets extends Controller {

  val list = List(
    Dataset(
      0,
      "DBpedia",
      2340000,
      List(
        Group(0, "dbpedia:Person", 700000),
        Group(1, "dbpedia:Place", 30000))
    ),
    Dataset(
      1,
      "DrugBank",
      5,
      List(
        Group(0, "Drugs", 3),
        Group(1, "Diseases", 2)
      )
    )
  )

  def datasets = Action {
    val data: List[Dataset] = list
    val json = Json.obj("datasets" -> data)
    Ok(json)
  }
}
