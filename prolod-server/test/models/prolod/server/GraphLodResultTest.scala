package models.prolod.server

import org.junit.Test
import org.hamcrest.CoreMatchers._
import org.hamcrest.MatcherAssert.assertThat
import play.api.libs.json.Json

import models.prolod.server.GraphLodResultFormats.{graphLodResultFormat, mapFormat}

class GraphLodResultTest {

  @Test def testDeserialize() {
      val result = GraphLodResult(0, nodeDegreeDistribution = Map(1->2, 2->5))
      val json = Json.obj("data" -> result)
      val str = Json.stringify(json)
      assertThat(str, equalTo("asd"))
  }

}
