package model.prolod.server

import play.api.libs.json.{Json, Writes}


case class Dataset(id: Int, name: String, size:Int, groups: List[Group])

case class Group(id: Int, name: String, size:Int)

object DatasetFormats{
  implicit val groupFormat = Json.format[Group]
  implicit val databaseSourceFormat = Json.format[Dataset]
}