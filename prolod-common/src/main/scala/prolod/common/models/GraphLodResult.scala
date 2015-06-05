package prolod.common.models

import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._

case class GraphLodResult(datasetId: Int,
                          var nodes: Int = 0,
                          var edges: Int = 0,
                          var connectedComponents: Int = 0,
                          var connectedComponentsMinEdges: Int = 0,
                          var connectedComponentsMaxEdges: Int = 0,
                          var connectedComponentsAvgEdges: Float = 0,
                          var stronglyConnectedComponents: Int = 0,
                          var stronglyConnectedComponentsMinEdges: Int = 0,
                          var stronglyConnectedComponentsMaxEdges: Int = 0,
                          var stronglyConnectedComponentsAvgEdges: Int = 0,
                          var averageDiameter: Float = 0,
                          var giantComponentEdges: Int = 0,
                          var giantComponentNodes: Int = 0,
                          var giantComponentDiameter: Float = 0,
                          var patterns: List[Pattern] = Nil,
                          nodeDegreeDistribution: Map[Int, Int] = Map(0 -> 0)
                          // val patternJson : HashMap[JSONObject, Integer] = new HashMap()

                           ) {

}

object GraphLodResultLoader {
  def load(datasetId: Integer) = {
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

  implicit val graphLodResultFormat = Json.format[GraphLodResult]

}
