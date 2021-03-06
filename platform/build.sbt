name := "platform"
 
version := "1.0"

lazy val `platform` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.11.12"
val hadoopVersion = "2.7.2"
val sparkVersion = "2.4.0"
val jacksonVersion = "2.8.7"

unmanagedResourceDirectories in Test += (baseDirectory.value / "target/web/public/test")

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % hadoopVersion
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % hadoopVersion

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonVersion

dependencyOverrides += "org.apache.hadoop" % "hadoop-common" % hadoopVersion
dependencyOverrides += "commons-io" % " commons-io" % "2.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test"
