package oyster.scavrodemo

import oyster.scavro.{AvroReader, AvroWriter}
import oyster.scavrodemo.model.LineItem


object ReadWriteDemo extends App {
  val filename = "invoice.avro"

  // Data setup
  val plywood = LineItem("Plywood table top", 13.95, 1)
  val boards = LineItem("2x4 raw lumber", 2.95, 2)
  val screws = LineItem("Wood Screws -- 3in No. 10", 0.065, 30)
  val varnish = LineItem("Wood varnish (can)", 3.95, 1)

  val materials = plywood :: boards :: screws :: varnish :: Nil

  // Convert to json
  materials.foreach(f => println(f.toJson))

  // Write the avro file
  val writer = AvroWriter[LineItem](filename)
  writer.write(materials)

  // Read the avro file and do some processing
  val reader: AvroReader[LineItem] = AvroReader[LineItem]
  val invoice = reader.read(filename)

  val total = invoice.map(li => li.quantity * li.price).sum
  println(f"-------------------------\nThe order total comes to $$$total%.2f")
}
