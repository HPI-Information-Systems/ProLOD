name := """prolod-common"""

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.0",
  "junit" % "junit" % "4.12" % "test",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.typesafe.slick" %% "slick-extensions" % "3.0.0",
  "com.ibm.db2" % "db2jcc" % "3.66.46"

// "com.typesafe.slick" %% "slick-codegen" % "3.0.0"
)

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/"
resolvers += Resolver.mavenLocal