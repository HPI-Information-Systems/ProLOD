name := """prolod-common"""

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "junit" % "junit" % "4.12" % "test" exclude("org.slf4j", "slf4j-log4j12"),
  "com.typesafe.slick" %% "slick" % "3.0.3" exclude("org.slf4j", "slf4j-log4j12"),
  "com.typesafe.slick" %% "slick-extensions" % "3.0.0" exclude("org.slf4j", "slf4j-log4j12")

  ,"keyness" % "keyness" % "0.1-SNAPSHOT"
    exclude("org.slf4j", "slf4j-log4j12")
    exclude("commons-logging", "commons-logging")
  ,"graphlod" % "graphlod" % "0.1-SNAPSHOT"
    exclude("org.slf4j", "slf4j-log4j12")
    exclude("commons-logging", "commons-logging")
    exclude("commons-configuration","commons-configuration")
    exclude("stax","stax-api")
  ,"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

//  "com.ibm.db2" % "db2jcc" % "3.66.46"

// "com.typesafe.slick" %% "slick-codegen" % "3.0.0"
)

