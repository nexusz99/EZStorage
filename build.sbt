name := """ezstorage"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.35"

libraryDependencies += "org.apache.tika" % "tika-core" % "1.8"

libraryDependencies += "net.coobird" % "thumbnailator" % "0.4.8"

libraryDependencies += "org.swinglabs" % "pdf-renderer" % "1.0.5"

fork in run := true