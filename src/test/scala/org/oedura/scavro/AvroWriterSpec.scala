package org.oedura.scavro

import java.io.File

import org.apache.avro.Schema
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

  it should "implement write method" in {
    val writer = AvroWriter[Number](new NullOutputStream())

    val list = Number("one", 1) :: Number("two", 2) :: Number("three", 3) :: Nil
    writer.write(list)
  }

}
