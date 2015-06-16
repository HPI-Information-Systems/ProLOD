name := """prolod-play"""


libraryDependencies ++= Seq(
  "org.webjars" % "angularjs" % "1.4.0",
  "org.webjars" % "requirejs" % "2.1.17",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "ui-grid" % "3.0.0-rc.21",
  "org.webjars" % "angular-tree-control" % "0.2.8",
  "org.webjars" % "d3js" % "3.5.3",
  "org.webjars" % "dimple" % "2.1.0",
  "org.webjars" % "angular-ui-bootstrap" % "0.13.0"
  //"org.webjars" % "jquery" % "2.1.4"
)

scalacOptions ++= Seq("-feature", "-language:reflectiveCalls")

pipelineStages := Seq( rjs,  digest, gzip)

// fork in run := true