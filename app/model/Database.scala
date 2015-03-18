package model

import play.api.libs.json.{Json, Writes}


case class DatabaseSource(name: String, groups: List[Group])

case class Group(name: String)

object Data {

  var list: List[DatabaseSource] = {
    List(
      DatabaseSource(
        "DBpedia",
        List(
          Group("humans"),
          Group("cars"))
      ),
      DatabaseSource(
        "Drugbank",
        List(
          Group("Drugs"),
          Group("Diseases")
        )
      )
    )

  }
}