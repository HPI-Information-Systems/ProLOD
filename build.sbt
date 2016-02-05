name := """ProLOD++"""

version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.7"

libraryDependencies ++= Seq(
	"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
)
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"

lazy val prolod_play = (project in file("prolod-play"))
  .enablePlugins(PlayScala)
  .dependsOn(prolod_server)

lazy val prolod_server = (project in file("prolod-server"))
  .enablePlugins(PlayScala)
  .dependsOn(prolod_common)

lazy val prolod_preprocessing = (project in file("prolod-preprocessing"))
  .dependsOn(prolod_common)

lazy val prolod_common = project in file("prolod-common")



// fork in run := true