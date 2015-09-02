package oyster.scavrodemo.model

import org.apache.avro.Schema
import oyster.scavrodemo.idl.{LineItem => JLineItem}
import oyster.scavro.{ AvroMetadata, AvroReader, AvroSerializeable}


case class LineItem(name: String, price: Double, quantity: Int) extends AvroSerializeable {
  type J = JLineItem
  override def toAvro: JLineItem = new JLineItem(name, price.toFloat, quantity)
}

object LineItem {
  implicit def reader = new AvroReader[LineItem] { override type J = JLineItem }

  implicit val metadata: AvroMetadata[LineItem, JLineItem] = new AvroMetadata[LineItem, JLineItem] {
    override val avroClass: Class[JLineItem] = classOf[JLineItem]
    override val schema: Schema = JLineItem.getClassSchema
    override val fromAvro: (JLineItem) => LineItem = (j: JLineItem) => {
      LineItem(j.getName.toString, j.getPrice.doubleValue, j.getQuantity)
    }
  }
}
