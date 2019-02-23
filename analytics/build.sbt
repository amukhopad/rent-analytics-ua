name := "analytics"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += {
  "org.apache.spark" %% "spark-core" % "2.4.0"
  "org.apache.spark" %% "spark-mllib" % "2.4.0"
}
