import sbt._
import sbt.Keys._
import oyster.scavro.plugin.AvroCodegenPlugin.autoImport._


object DemoBuild extends Build {
  lazy val demoSettings = Defaults.defaultSettings ++ baseAvroCodegenSettings ++ Seq(
    // General settings
    organization := "oyster",
    scalaVersion := "2.10.4",
    version := "0.0.2",
    libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % "1.7.7",
      "org.apache.avro" % "avro-tools" % "1.7.7",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    ),
    resolvers ++= Seq(
      "Local Maven" at Path.userHome.asFile.toURI.toURL + ".ivy2/local",
      Resolver.sonatypeRepo("releases")
    ),

    // scavro plugin settings
    avroSchemaFiles := Seq(
      (resourceDirectory in Compile).value / "item.avsc"
    ),

    mainClass in (Compile, run) := Some("oyster.scavrodemo.ReadWriteDemo")
  )

  // TODO: Remove this and replace with maven libraryDependency once published
  lazy val scavroProject = RootProject(file("../"))

  lazy val root = Project(id = "demo", base = file("."))
    .dependsOn(scavroProject)
    .settings(demoSettings: _*)
    .settings(excludeFilter in unmanagedResources := "*.avsc")
}
