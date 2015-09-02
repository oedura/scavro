package oyster.scavro

import java.io.{FileOutputStream, InputStream}

import sbt._

import scala.io.Source
import scala.util.Random

class TestUtils(workingDir: File) {
  (workingDir / "in").mkdir
  (workingDir / "out").mkdir

  def tmpDir = workingDir
  def tmpPath = workingDir.getAbsolutePath

  private def extractResource(resourceName: String): File = {
    val is: InputStream = getClass.getResourceAsStream(s"/$resourceName")
    val text = Source.fromInputStream(is).mkString
    val os: FileOutputStream = new FileOutputStream(workingDir / "in" / resourceName)
    os.write(text.getBytes)
    os.close()
    is.close()

    workingDir / "in" / resourceName
  }

  lazy val schemaFile: File = extractResource("Number.avsc")
  lazy val protocolFile: File = {
    schemaFile
    extractResource("NumberSystem.avdl")
  }

  def cleanup() = {
    def getRecursively(f: File): Seq[File] = f.listFiles.filter(_.isDirectory).flatMap(getRecursively) ++ f.listFiles

    getRecursively(workingDir).foreach { f =>
      if (!f.delete()) throw new RuntimeException("Failed to delete " + f.getAbsolutePath)
    }
    tmpDir.delete()
  }
}

object TestUtils {
  private val alphabet = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')

  def randomFile(dir: File, prefix: String = "", suffix: String = "", maxTries: Int = 100, nameSize: Int = 10): File = {
    def randomFileImpl(triesLeft: Int): String = {
      val testName: String = (1 to nameSize).map(_ => alphabet(Random.nextInt(alphabet.size))).mkString
      if (!(dir / (prefix + testName + suffix)).exists) prefix + testName + suffix
      else if (triesLeft < 0) throw new Exception("Unable to find empty random file path.")
      else randomFileImpl(triesLeft - 1)
    }

    dir / randomFileImpl(maxTries)
  }

  def randomFileName(prefix: String, suffix: String = "", maxTries: Int = 100, nameSize: Int = 10): String = {
    def randomFileNameImpl(triesLeft: Int): String = {
      val testName: String = (1 to nameSize).map(_ => alphabet(Random.nextInt(alphabet.size))).mkString
      if (!file(prefix + testName + suffix).exists) prefix + testName + suffix
      else if (triesLeft < 0) throw new Exception("Unable to find empty random file path.")
      else randomFileNameImpl(triesLeft - 1)
    }

    randomFileNameImpl(maxTries)
  }

  def apply(workingDir: File) = {
    if (workingDir.exists && workingDir.isDirectory) new TestUtils(workingDir)
    else if (!workingDir.exists) {
      val success = workingDir.mkdirs
      if (success) new TestUtils(workingDir)
      else throw new Exception("Cannot initialize working directory")
    } else throw new Exception("Requested directory is occupied by ordinary file")
  }

}
