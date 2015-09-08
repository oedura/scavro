package com.oysterbooks.scavro

import org.apache.avro.specific.SpecificRecordBase


 /**
  * [[AvroSerializeable]] is the base trait of scala classes to be serialized or deserialized through the
  * [[AvroReader]] and [[AvroWriter]] classes.
  */
trait AvroSerializeable {
  /** Type of corresponding Java serialization class. */
  type J <: SpecificRecordBase

  /** Returns an equivalent instance of the corresponding Java serialization class. */
  def toAvro: J

  /** Returns json encoding of this instance. */
  def toJson: String = toAvro.toString
}
