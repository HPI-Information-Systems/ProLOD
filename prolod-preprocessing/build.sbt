name := "prolod-preprocessing"

libraryDependencies ++= Seq(
  "graphlod" % "graphlod" % "0.1-SNAPSHOT"
)

// for sbt-dependency-graph
net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers += Resolver.mavenLocal

mainClass in (Compile, run)  := Some("prolod.preprocessing.ImportDataset")