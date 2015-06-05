name := "prolod-preprocessing"


libraryDependencies ++= Seq(
  "graphlod" % "graphlod" % "0.1-SNAPSHOT"
)

resolvers += Resolver.mavenLocal

mainClass in (Compile, run)  := Some("prolod.preprocessing.Main")