package com.oysterbooks.scavro.plugin

import sbt.Keys._
import sbt._


/** SBT Plugin to automate Avro code generation of Java classes */
object AvroCodegenPlugin extends AutoPlugin {
  object autoImport {
    val avroSchemaFiles = settingKey[Seq[File]]("Avro schema files to compile")
    val avroProtocolFiles = settingKey[Seq[File]]("Avro Protocol files to compile")
    val avroIDLFiles = settingKey[Seq[File]]("Avro IDL files to compile")
    val avroDataFiles = settingKey[Seq[File]]("Avro datafiles to compile")

    val showAvroCompilerOutput = settingKey[Boolean]("Hides messages from avro compiler")

    val avroCodeOutputDirectory = settingKey[File]("Location where generated Java code will be placed")
    val avroScalaCodeOutputDirectory = settingKey[File]("Location where generated Scala wrapper code will be placed")

    val avroScalaCustomTypes = settingKey[Map[String, Class[_]]]("Customize Avro to Scala type map by type")
    val avroScalaCustomNamespace = settingKey[Map[String, String]]("Custom namespace of generated Scala wrapper code")

    val avroCodegenTask = taskKey[Unit]("Compiles AVRO files")

    lazy val baseAvroCodegenSettings: Seq[Def.Setting[_]] = Seq(
      avroSchemaFiles := Seq.empty[File],
      avroProtocolFiles := Seq.empty[File],
      avroIDLFiles := Seq.empty[File],
      avroDataFiles := Seq.empty[File],
      showAvroCompilerOutput := false,
      avroCodeOutputDirectory := baseDirectory.value / "src" / "main" / "java",
      avroScalaCodeOutputDirectory := baseDirectory.value / "src" / "main" / "scala",
      avroScalaCustomTypes := Map.empty[String, Class[_]],
      avroScalaCustomNamespace := Map.empty[String, String],  
      avroCodegenTask := {
        println("running codegen")
        val compiler = AvroCodegen(avroCodeOutputDirectory.value, file("/tmp"), showAvroCompilerOutput.value)
        compiler.run(avroIDLFiles.value, avroProtocolFiles.value, avroSchemaFiles.value, avroDataFiles.value)
        compiler.compileSchema(avroSchemaFiles.value)
        val generator = ScalaCodegen(avroScalaCodeOutputDirectory.value, 
          file("/tmp"), 
          showAvroCompilerOutput.value,
          avroScalaCustomTypes.value,
          avroScalaCustomNamespace.value)
        generator.run(avroIDLFiles.value, avroProtocolFiles.value, avroSchemaFiles.value, avroDataFiles.value)
      },
      compile <<= (compile in Compile) dependsOn avroCodegenTask
    )
  }

  import autoImport._

  override val projectSettings = inConfig(Compile)(baseAvroCodegenSettings) ++
    inConfig(Test)(baseAvroCodegenSettings)

}
