package com.oysterbooks.scavro

import com.oysterbooks.scavro.plugin.ScalaCodegen
import org.scalatest._

import sbt._

/**
 * To run this test: ./sbt test
 */
class ScalaCodegenSpec extends FlatSpec {

  "Temporary file path" should "replace .avdl suffix with .avpr" in {
    val scg = ScalaCodegen(file(""), file("/tmp"))
    assert(scg.getTmpFile(file("test.avdl")).getName == "test.avpr")
  }

  it should "replace .avdl with .avpr in the requested directory" in {
    val scg = ScalaCodegen(file(""), file("/tmp"))
    assert(scg.getTmpFile(file("test.avdl")).getPath == "/tmp/test.avpr")
  }

  it should "accept a specified temporary file" in {
    val scg = ScalaCodegen(file(""), file("/tmp/foo.avpr"))
    assert(scg.getTmpFile(file("test.avdl")).getPath == "/tmp/foo.avpr")
  }

  "ScalaCodegen" should "compile a schema file" in {
    val tu = TestUtils(TestUtils.randomFile(file("/tmp"), "avro_codegen_test_"))
    val scg = ScalaCodegen(tu.tmpDir / "out", tu.tmpDir / "tmp.avpr")
    scg.run(Seq.empty[File], Seq.empty[File], Seq(tu.schemaWithNamespaceFile), Seq.empty[File])
    assert((tu.tmpDir / "out" / "example" / "model" / "User.scala").exists)
    tu.cleanup()
  }

  it should "compile a datafile" in {
    val tu = TestUtils(TestUtils.randomFile(file("/tmp"), "avro_codegen_test_"))
    val scg = ScalaCodegen(tu.tmpDir / "out", tu.tmpDir )
    scg.run(Seq.empty[File], Seq.empty[File], Seq.empty[File], Seq(tu.avroDataFile))
    assert((tu.tmpDir / "out" / "com" / "oysterbooks" / "scavrodemo" /  "idl" / "model" / "LineItem.scala").exists)
    tu.cleanup()
  }
  

}
