name := "platorm"
 
version := "1.0" 

lazy val `platorm` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += {
  "org.apache.spark" %% "spark-core" % "2.4.0"
  "org.apache.spark" %% "spark-mllib" % "2.4.0"
}
