package model

import play.api.libs.json.{Json, Writes}


case class Dataset(name: String, groups: List[Group])

case class Group(name: String)