package prolod.common.models

import play.api.libs.json.Json

case class Dataset(id: Int, name: String, size:Int, groups: List[Group])

case class Group(id: Int, name: String, size:Int)

object DatasetFormats{
  implicit val groupFormat = Json.format[Group]
  implicit val datasetFormat = Json.format[Dataset]

}