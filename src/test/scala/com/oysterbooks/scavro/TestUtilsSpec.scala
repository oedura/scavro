package com.oysterbooks.scavro

import com.oysterbooks.scavro.plugin.NullOutputStream
import org.scalatest._
import sbt.file

/**
 * To run this test: ./sbt test
 */
class TestUtilsSpec extends FlatSpec with Matchers {
  "Random file" should "have correct suffix" in {
    val testFile = TestUtils.randomFile(file("/tmp"), "x_", ".foo")
    assert(testFile.getName.endsWith(".foo"))
  }

  it should "have correct prefix" in {
    val testFile = TestUtils.randomFile(file("/tmp"), "x_", ".foo")
    assert(testFile.getAbsolutePath.startsWith("/tmp/x_"))
  }

  it should "not exist" in {
    val testFile = TestUtils.randomFile(file("/tmp"), ".foo")
    assert(!testFile.exists)
  }

  it should "not return the same value on every invocation" in {
    val file_1 = TestUtils.randomFile(file("/tmp"), ".foo")
    val file_2 = TestUtils.randomFile(file("/tmp"), ".foo")
    assert(file_1.getName != file_2.getName)
  }

  it should "be the correct length" in {
    val testFile = TestUtils.randomFile(file("/tmp"), "", ".test", 100, 4)
    testFile.getName should have length 9
  }

  "Random file name" should "accept string prefixes" in {
    noException should be thrownBy {
      val testFile = TestUtils.randomFileName("/tmp", ".foo")
    }
  }

  val tmpFile = TestUtils.randomFile(file("/tmp"), "avro_codegen_test_")

  "TestUtils" should "create AVSC file" in {
    val util = TestUtils(tmpFile)
    assert(util.schemaFile.exists())
    assert(util.schemaFile.length > 0)
    util.cleanup()
  }

  it should "create schema file only once" in {
    val util = TestUtils(tmpFile)
    val schema = util.schemaFile.getAbsolutePath
    assert(schema == util.schemaFile.getAbsolutePath)
    util.cleanup()
  }

  it should "remove temporary files" in {
    val util = TestUtils(tmpFile)
    val schema = util.schemaFile.getAbsolutePath
    util.cleanup()
    assert(!tmpFile.exists)
  }

  "NullOutputStream" should "do nothing on write" in {
    val nos = new NullOutputStream()
    nos.write(0)
    val byteArray = new Array[Byte](2)
    byteArray(0) = 0
    byteArray(1) = 1
    nos.write(byteArray)
    nos.write(byteArray, 0, 2)
    nos.close()
  }
}
