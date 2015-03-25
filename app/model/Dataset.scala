package model

import play.api.libs.json.{Json, Writes}


case class Dataset(name: String, size:Int, groups: List[Group])

case class Group(name: String, size:Int)

object DatasetFormats{
  implicit val groupFormat = Json.format[Group]
  implicit val databaseSourceFormat = Json.format[Dataset]
}