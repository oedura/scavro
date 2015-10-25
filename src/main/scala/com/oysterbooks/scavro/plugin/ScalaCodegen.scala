package com.oysterbooks.scavro.plugin

import java.io.PrintStream

import avrohugger.format.Scavro
import avrohugger.tool.GeneratorTool
import org.apache.avro.tool.{IdlTool, Tool}
import sbt._

import scala.collection.JavaConversions._


/** Scala wrapper for code generation from avro-tools */
class ScalaCodegen(outputDir: File, 
  tmpDir: File, 
  verbose: Boolean, 
  scalaCustomTypes: Map[String, String] = Map.empty,
  scalaCustomNamespace: Map[String, String] = Map.empty) {
  val generatorTool = new GeneratorTool(Scavro, scalaCustomTypes, scalaCustomNamespace)
  val idlTool = new IdlTool()

  val outputPath = outputDir.getAbsolutePath

  def run(idlFiles: Seq[File], 
    protocolFiles: Seq[File], 
    schemaFiles: Seq[File], 
    dataFiles: Seq[File]) = {
    idlFiles.foreach(compileIDL)
    protocolFiles.foreach(compileProtocol)
    compileSchema(schemaFiles)
    compileDatafile(dataFiles)
  }

  def outputStream(default: PrintStream) = {
    if (verbose) default
    else NullOutputStream.getPrintStream
  }

  def runTool(tool: Tool, args: List[String]) = {
    val params = args.toBuffer[String]
    tool.run(System.in, outputStream(System.out), outputStream(System.err), args)
  }

  def getTmpFile(target: File): File = {
    if (tmpDir.isDirectory) {
      val idlFileNameRegex = """(.*)\.avdl""".r
      val tmpFileName = target.getName match {
        case idlFileNameRegex(fname) => s"$fname.avpr"
      }

      tmpDir / tmpFileName
    } else {
      tmpDir
    }
  }

  def compileSchema(input: Seq[File]) = if (input.nonEmpty) {
    println("compile schema: " + input)
    val compilerParams: List[String] = "schema" +: 
      input.toList.map(_.getAbsolutePath) :+ outputPath
    runTool(generatorTool, compilerParams)
  }

  def compileProtocol(input: File) = {
    val compilerParams: List[String] = "protocol" :: input.getAbsolutePath :: outputPath :: Nil
    runTool(generatorTool, compilerParams)
  }

  def compileIDL(input: File) = {
    val tmpFile = getTmpFile(input)
    val idlParams = input.getAbsolutePath :: tmpFile.getAbsolutePath :: Nil
    runTool(idlTool, idlParams)
    compileProtocol(tmpFile)

    tmpFile.delete()
  }

  def compileDatafile(input: Seq[File]) = {
    println("compile datafile: " + input)
    val datafileParams: List[String] = "datafile" +: input.toList.map(_.getAbsolutePath) :+ outputPath
    runTool(generatorTool, datafileParams)
  }
}

object ScalaCodegen {
  def apply(outputDir: File, 
    tmpDir: File, 
    verbose: Boolean = false,
    scalaCustomTypes: Map[String, String] = Map.empty,
    scalaCustomNamespace: Map[String, String] = Map.empty) = {
    new ScalaCodegen(outputDir, tmpDir, verbose, scalaCustomTypes, scalaCustomNamespace)
  }
}

