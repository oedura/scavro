package org.oedura.scavro

import java.io.File

import org.scalatest.FlatSpec

class AvroWriterSpec extends FlatSpec {

  "AvroWriter" should "implement factory methods" in {
    val outputStreamWriter = AvroWriter[Number](new NullOutputStream())
    val fileNameWriter = AvroWriter[Number]("/dev/null")
    val fileWriter = AvroWriter[Number](new File("/dev/null"))

    assert(outputStreamWriter.isInstanceOf[AvroWriter[_]])
    assert(fileNameWriter.isInstanceOf[AvroWriter[_]])
    assert(fileWriter.isInstanceOf[AvroWriter[_]])
  }

}
