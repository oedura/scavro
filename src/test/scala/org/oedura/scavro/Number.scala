package org.oedura.scavro

import org.apache.avro.Schema

/* Mock class for reading/writing */
case class Number(name: String, value: Int) extends AvroSerializeable {
  type J = MockNumber
  override def toAvro: MockNumber = new MockNumber(name, value)
}

object Number {
  implicit def reader = new AvroReader[Number] { override type J = MockNumber }

  implicit val metadata = new AvroMetadata[Number, MockNumber] {
    override val avroClass = classOf[MockNumber]
    override val schema: Schema = MockNumber.getClassSchema
    override val fromAvro: (MockNumber) => Number = { mock =>
      val name: String = mock.get(0).asInstanceOf[String]
      val value: Int = mock.get(1).asInstanceOf[Int]
      Number(name, value)
    }
  }
}