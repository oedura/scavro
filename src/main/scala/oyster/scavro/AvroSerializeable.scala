package oyster.scavro

import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase

trait AvroSerializeable {
  type J <: SpecificRecordBase
  def toJson: String = toAvro.toString
  def toAvro: J
}

trait AvroMetadata[S, J] {
  val avroClass: Class[J]
  val schema: Schema
  val fromAvro: (J) => S
}
