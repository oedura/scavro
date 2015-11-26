package org.oedura.scavro

import org.oedura.scavro.plugin.AvroCodegen
import org.scalatest._

import sbt._

/**
 * To run this test: ./sbt test
 */
class AvroCodegenSpec extends FlatSpec {

  "Temporary file path" should "replace .avdl suffix with .avpr" in {
    val acg = AvroCodegen(file(""), file("/tmp"))
    assert(acg.getTmpFile(file("test.avdl")).getName == "test.avpr")
  }

  it should "replace .avdl with .avpr in the requested directory" in {
    val acg = AvroCodegen(file(""), file("/tmp"))
    assert(acg.getTmpFile(file("test.avdl")).getPath == "/tmp/test.avpr")
  }

  it should "accept a specified temporary file" in {
    val acg = AvroCodegen(file(""), file("/tmp/foo.avpr"))
    assert(acg.getTmpFile(file("test.avdl")).getPath == "/tmp/foo.avpr")
  }

  "AvroCodegen" should "compile a schema file" in {
    val tu = TestUtils(TestUtils.randomFile(file("/tmp"), "avro_codegen_test_"))
    val acg = AvroCodegen(tu.tmpDir / "out", tu.tmpDir / "tmp.avpr")
    acg.run(Seq.empty[File], Seq.empty[File], Seq(tu.schemaFile))
    assert((tu.tmpDir / "out" / "Number.java").exists)
    tu.cleanup()
  }

  it should "compile a protocol file" in {
    val tu = TestUtils(TestUtils.randomFile(file("/tmp"), "avro_codegen_test_"))
    val acg = AvroCodegen(tu.tmpDir / "out", tu.tmpDir)
    acg.run(Seq(tu.protocolFile), Seq.empty[File], Seq.empty[File])
    assert((tu.tmpDir / "out" / "Number.java").exists)
    assert((tu.tmpDir / "out" / "NumberSystem.java").exists)
    assert((tu.tmpDir / "out" / "NumberSystemProtocol.java").exists)
    tu.cleanup()
  }
}
