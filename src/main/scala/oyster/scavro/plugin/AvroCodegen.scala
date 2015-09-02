package oyster.scavro.plugin

import java.io.PrintStream

import org.apache.avro.tool.{IdlTool, SpecificCompilerTool, Tool}
import sbt._

import scala.collection.JavaConversions._


class AvroCodegen(outputDir: File, tmpDir: File, verbose: Boolean) {
  val compilerTool = new SpecificCompilerTool()
  val idlTool = new IdlTool()

  val outputPath = outputDir.getAbsolutePath

  def run(idlFiles: Seq[File], protocolFiles: Seq[File], schemaFiles: Seq[File]) = {
    idlFiles.foreach(compileIDL)
    protocolFiles.foreach(compileProtocol)
    compileSchema(schemaFiles)
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
    val compilerParams: List[String] = "schema" +: input.toList.map(_.getAbsolutePath) :+ outputPath
    runTool(compilerTool, compilerParams)
  }

  def compileProtocol(input: File) = {
    val compilerParams: List[String] = "protocol" :: input.getAbsolutePath :: outputPath :: Nil
    runTool(compilerTool, compilerParams)
  }

  def compileIDL(input: File) = {
    val tmpFile = getTmpFile(input)
    val idlParams = input.getAbsolutePath :: tmpFile.getAbsolutePath :: Nil
    runTool(idlTool, idlParams)
    compileProtocol(tmpFile)

    tmpFile.delete()
  }
}

object AvroCodegen {
  def apply(outputDir: File, tmpDir: File, verbose: Boolean = false) = new AvroCodegen(outputDir, tmpDir, verbose)
}
