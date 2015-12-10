/** MACHINE-GENERATED FROM AVRO SCHEMA. DO NOT EDIT DIRECTLY */
package com.oysterbooks.scavrodemo.model

import org.apache.avro.Schema

import com.oysterbooks.scavro.{AvroMetadata, AvroReader, AvroSerializeable}

import com.oysterbooks.scavrodemo.idl.{LineItem => JLineItem}

case class LineItem(name: String, price: Double, quantity: Int) extends AvroSerializeable {
  type J = JLineItem
  override def toAvro: JLineItem = {
    new JLineItem(name, price.toFloat, quantity)
  }
}

object LineItem {
  implicit def reader = new AvroReader[LineItem] {
    override type J = JLineItem
  }
  implicit val metadata: AvroMetadata[LineItem, JLineItem] = new AvroMetadata[LineItem, JLineItem] {
    override val avroClass: Class[JLineItem] = classOf[JLineItem]
    override val schema: Schema = JLineItem.getClassSchema()
    override val fromAvro: (JLineItem) => LineItem = {
      (j: JLineItem) => LineItem(j.getName.toString, j.getPrice.toFloat, j.getQuantity.toInt)
    }
  }
}