package prolod.common.models

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

case class ComponentInfo(var count: Int = 0,
                         var minEdges: Int = 0,
                         var maxEdges: Int = 0,
                         var avgEdges: Int = 0)

case class GiantComponentInfo(var edges: Int = 0,
                              var nodes: Int = 0,
                              var diameter: Int = 0)

case class GraphLodResult(datasetId: String,
                          var nodes: Int = 0,
                          var edges: Int = 0,
                          var connectedComponents: ComponentInfo = ComponentInfo(),
                          var stronglyConnectedComponents: ComponentInfo = ComponentInfo(),
                          var averageDiameter: Float = 0,
                          var averageLinks: Float = 0,
                          var diameter: Int = 0,
                          var giantComponent: GiantComponentInfo = GiantComponentInfo(),
                          var patterns: List[Pattern] = Nil,
                          var patternTypes: Map[String, Int] = Map(),
                          var nodeDegreeDistribution: Map[Int, Int] = Map(0 -> 0),
                          var classDistribution: Map[String, Double] = Map(),
                          var highestIndegrees: Map[String, Map[Int, Int]] = Map(),
                          var highestOutdegrees: Map[String, Map[Int, Int]] = Map()
                         )

object GraphLodResultLoader {
    def load(datasetId: String) = {
        new GraphLodResult(datasetId)
    }
}

object GraphLodResultFormats {
    implicit val patternFormat = PatternFormats.patternFormat

    implicit val mapReads: Reads[Map[Int, Int]] = new Reads[Map[Int, Int]] {
        def reads(jv: JsValue): JsResult[Map[Int, Int]] =
            JsSuccess(jv.as[Map[String, Int]].map { case (k, v) =>
                Integer.parseInt(k) -> v.asInstanceOf[Int]
            })
    }

    implicit val mapWrites: Writes[Map[Int, Int]] = new Writes[Map[Int, Int]] {
        def writes(map: Map[Int, Int]): JsValue =
            Json.obj(map.map { case (s, o) =>
                val ret: (String, JsValueWrapper) = s.toString -> JsNumber(o)
                ret
            }.toSeq: _*)
    }

    implicit val mapIntIntFormat: Format[Map[Int, Int]] = Format(mapReads, mapWrites)

    implicit val giantComponentInfoFormat = Json.format[GiantComponentInfo]

    implicit val componentInfoFormat = Json.format[ComponentInfo]

    implicit val graphLodResultFormat = Json.format[GraphLodResult]
}