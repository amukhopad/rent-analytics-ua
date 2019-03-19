name := "analytics"

version := "1.0"

scalaVersion := "2.11.12"
val sparkVersion = "2.4.0"
val vegasVersion = "0.3.11"
val hadoopVersion = "2.7.2"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % hadoopVersion

libraryDependencies += "org.vegas-viz" %% "vegas" % vegasVersion
libraryDependencies += "org.vegas-viz" %% "vegas-spark" % vegasVersion

