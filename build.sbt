sbtPlugin := true

name := "scavro"

organization := "oyster"

version := "0.0.5-SNAPSHOT"

scalaVersion := "2.10.4"

publishTo := Some("Local Maven" at Path.userHome.asFile.toURI.toURL + ".ivy2/local")

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.7.7",
  "org.apache.avro" % "avro-tools" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
