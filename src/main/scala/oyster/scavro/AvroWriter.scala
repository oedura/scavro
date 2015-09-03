package oyster.scavro

import java.io._

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.{ SpecificDatumWriter, SpecificRecordBase }


class AvroWriter[S <: AvroSerializeable](outs: OutputStream) {
  def write(items: Seq[AvroSerializeable])(implicit m: AvroMetadata[S, S#J]) = {
    def writeToStream(outs: OutputStream) = {
      val avroItems: Seq[S#J] = items.map(_.toAvro.asInstanceOf[S#J])
      val datumWriter = new SpecificDatumWriter[S#J](m.avroClass)
      val datumFileWriter = new DataFileWriter[S#J](datumWriter)

      datumFileWriter.create(m.schema, outs)
      avroItems.foreach(item => datumFileWriter.append(item))
      datumFileWriter.close()
    }

    writeToStream(outs)
  }
}

object AvroWriter {
  def apply[S <: AvroSerializeable](outs: OutputStream) = new AvroWriter[S](outs)
  def apply[S <: AvroSerializeable](file: File) = new AvroWriter[S](new FileOutputStream(file))
  def apply[S <: AvroSerializeable](filename: String) = {
    new AvroWriter[S](new FileOutputStream(new File(filename)))
  }
}
