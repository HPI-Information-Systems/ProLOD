name := "prolod-preprocessing"

libraryDependencies ++= Seq(
  "graphlod" % "graphlod" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12"),
  "keyness" % "keyness" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12"),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
)


// for sbt-dependency-graph
net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers += Resolver.mavenLocal
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"

// resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"

mainClass in (Compile, run)  := Some("prolod.preprocessing.ImportDataset")