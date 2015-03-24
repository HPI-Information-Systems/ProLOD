package model

import play.api.libs.json.{Json, Writes}


case class Dataset(name: String, groups: List[Group])

case class Group(name: String)

object DatasetFormats{
  implicit val groupFormat = Json.format[Group]
  implicit val databaseSourceFormat = Json.format[Dataset]
}