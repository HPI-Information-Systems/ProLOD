package prolod.common.server

import org.junit.Test
import org.hamcrest.CoreMatchers._
import org.hamcrest.MatcherAssert.assertThat
import play.api.libs.json.Json
import prolod.common.GraphLodResultFormats
import prolod.common.models.{GraphLodResultFormats, GraphLodResult}

import GraphLodResultFormats.graphLodResultFormat
import prolod.common.models.GraphLodResult

class GraphLodResultTest {

  @Test def testDeserialize() {
      val result = GraphLodResult(0, nodeDegreeDistribution = Map(1->2, 2->5))
      val json = Json.obj("data" -> result)
      val str = Json.stringify(json)
      assertThat(str, equalTo("asd"))
  }


  @Test def testSerialize() {
      val result = GraphLodResult(0, nodeDegreeDistribution = Map(1->2, 2->5))
      val str = Json.stringify(Json.obj("data" -> result))
      val json = Json.parse(str)
      (json / )
      assertThat(str, equalTo("asd"))
  }

}
