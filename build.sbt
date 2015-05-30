name := """Prolod2"""

version := "1.0-SNAPSHOT"


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