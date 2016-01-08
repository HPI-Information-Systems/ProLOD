name := "prolod-preprocessing"

libraryDependencies ++= Seq(
  "graphlod" % "graphlod" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12"),
  "keyness" % "keyness" % "0.1-SNAPSHOT" exclude("org.slf4j", "slf4j-log4j12"),
  "com.typesafe.akka" %% "akka-actor" % "2.4.1"
)

// for sbt-dependency-graph
net.virtualvoid.sbt.graph.Plugin.graphSettings

resolvers += Resolver.mavenLocal

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

mainClass in (Compile, run)  := Some("prolod.preprocessing.AkkaMain")