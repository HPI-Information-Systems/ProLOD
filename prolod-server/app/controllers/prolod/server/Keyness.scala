package controllers.prolod.server

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import prolod.common.config.{Configuration, DatabaseConnection}
import prolod.common.models.KeynessResultFormats.keynessResultFormat
import prolod.common.models._

object Keyness extends Controller {
  def getKeyness(datasetId: String, groups: List[String]) = Action {
    val config = new Configuration()
    val db = new DatabaseConnection(config)
    var keyness : List[KeynessResult] = db.getKeyness(datasetId, groups)
    //val data: KeynessResult = KeynessResult(datasetId, keyness)
    /*
    val keyness = db.getKeyness(datasetId)

    data.keyness = keyness.getOrElse("keyness", 0)
    data.density = keyness.getOrElse("density", 0)
    data.uniqueness = keyness.getOrElse("uniqueness", 0)
      */
    // val keyness = new KeynessResult()
    val json = Json.obj("data" -> keyness)
    Ok(json)
  }
}
