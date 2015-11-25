package org.oedura.scavro

import org.apache.avro.Schema


 /**
  * Provides a mapping between the Scala wrapper class `S`, which should extend [[AvroSerializeable]], and the
  * corresponding Java class `J`, which should extend `org.apache.avro.specific.SpecificRecordBase`.  Typically an
  * an implicit instance of this trait would be defined on the companion object of a class extending
  * [[AvroSerializeable]], and `J` would be a Java class produced by Avro code generation.
  *
  * An implicit instance with this trait will provide to Avro values that would in a Java application be provided by
  * static members of the `SpecificRecordBase`.
  *
  * @tparam S  Scala class
  * @tparam J  Java class from Avro code generation
  */
trait AvroMetadata[S, J] {
  val avroClass: Class[J]
  val schema: Schema
  /** Function that can produce an instance of type `S` from an instance of type `J` */
  val fromAvro: (J) => S
}
