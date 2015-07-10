name := "prolod-preprocessing"

libraryDependencies ++= Seq(
  "graphlod" % "graphlod" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12"),
  "keyness" % "keyness" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12")
)

// for sbt-dependency-graph
net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers += Resolver.mavenLocal

mainClass in (Compile, run)  := Some("prolod.preprocessing.ImportDataset")