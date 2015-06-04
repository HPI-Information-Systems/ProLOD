package controllers.prolod.server

import play.api.mvc.{Action, Controller}
import play.api.libs.json._

object Datasets extends Controller {



	def list = Action {
		val list : List[String] = List("DBpedia", "DrugBank")
		implicit val json = Json.format[List]
		Ok(json)
	}


}
