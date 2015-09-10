sbtPlugin := true

// Metadata
organization := "com.oysterbooks"
name := "scavro"
version := "0.9.0"
scalaVersion := "2.10.4"
description := "An SBT plugin for automatically calling Avro code generation " +
  "and a thin scala wrapper for reading and writing Avro files"

// Distribution
licenses := Seq("Apache" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/oysterbooks/scavro"))

// Dependencies
resolvers += Resolver.sonatypeRepo("public")
libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.7.7",
  "org.apache.avro" % "avro-tools" % "1.7.7",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Publication
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  println("isSnapshot: " + isSnapshot.value)
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := {
  <scm>
    <url>git@github.com:oysterbooks/scavro.git</url>
    <connection>scm:git@github.com:oysterbooks/scavro.git</connection>
  </scm>
  <developers>
    <developer>
      <id>BrianLondon</id>
      <name>Brian London</name>
      <url>https://github.com/BrianLondon</url>
    </developer>
  </developers>
}
