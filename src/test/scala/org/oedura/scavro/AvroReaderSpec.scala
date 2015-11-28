package org.oedura.scavro

import org.scalatest.FlatSpec


class AvroReaderSpec extends FlatSpec {
  "AvroReader" should "implement factory method" in {
    val reader = AvroReader[Number]
    assert(reader.isInstanceOf[AvroReader[_]])
  }
}
