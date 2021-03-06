package org.oedura.scavro.plugin

import sbt.Keys._
import sbt._


/** SBT Plugin to automate Avro code generation of Java classes */
object AvroCodegenPlugin extends AutoPlugin {
  object autoImport {
    val avroSchemaFiles = settingKey[Seq[File]]("Avro schema files to compile")
    val avroProtocolFiles = settingKey[Seq[File]]("Avro Protocol files to compile")
    val avroIDLFiles = settingKey[Seq[File]]("Avro IDL files to compile")

    val showAvroCompilerOutput = settingKey[Boolean]("Hides messages from avro compiler")

    val avroCodeOutputDirectory = settingKey[File]("Location where generated Java code will be placed")

    val avroCodegenTask = taskKey[Unit]("Compiles AVRO files")

    lazy val baseAvroCodegenSettings: Seq[Def.Setting[_]] = Seq(
      avroSchemaFiles := Seq.empty[File],
      avroProtocolFiles := Seq.empty[File],
      avroIDLFiles := Seq.empty[File],
      showAvroCompilerOutput := false,
      avroCodeOutputDirectory := baseDirectory.value / "src" / "main" / "java",     
      avroCodegenTask := {
        println("running codegen")
        val compiler = AvroCodegen(avroCodeOutputDirectory.value, file("/tmp"), showAvroCompilerOutput.value)
        compiler.run(avroIDLFiles.value, avroProtocolFiles.value, avroSchemaFiles.value)
      },
      compile <<= (compile in Compile) dependsOn avroCodegenTask
    )
  }

  import autoImport._

  override val projectSettings = inConfig(Compile)(baseAvroCodegenSettings) ++
    inConfig(Test)(baseAvroCodegenSettings)

}
