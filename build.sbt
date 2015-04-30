name := """Prolod2"""

version := "1.0-SNAPSHOT"

lazy val prolod_play = (project in file("prolod-play"))
  .enablePlugins(PlayScala)
  .aggregate(prolod_server)
  .dependsOn(prolod_server)

lazy val prolod_server = (project in file("prolod-server"))
    .aggregate(prolod_common)
    .dependsOn(prolod_common)

lazy val prolod_preprocessing = (project in file("prolod-preprocessing"))
    .aggregate(prolod_common)
    .dependsOn(prolod_common)

lazy val prolod_common = project in file("prolod-common")


// fork in run := true