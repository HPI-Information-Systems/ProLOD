name := """Prolod2"""

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" % "angularjs" % "1.3.0-beta.2",
  "org.webjars" % "requirejs" % "2.1.11-1",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "ui-grid" % "3.0.0-rc.20",
  "org.webjars" % "angular-tree-control" % "0.2.8",
  "org.webjars" % "angular-chart.js" % "0.5.3",
  "org.webjars" % "d3js" % "3.5.3"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

pipelineStages := Seq(rjs, digest, gzip)


// fork in run := true