package oyster.scavro

import java.io._

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.{ SpecificDatumWriter, SpecificRecordBase }


class AvroWriter[S <: AvroSerializeable](file: File) {
  def write(items: Seq[AvroSerializeable])(implicit m: AvroMetadata[S, S#J]) = {

    def writeFile(): Unit = {
      val outs = new FileOutputStream(file)
      writeToStream(outs)
    }

    def writeToStream(outs: OutputStream) = {
      val avroItems: Seq[S#J] = items.map(_.toAvro.asInstanceOf[S#J])
      val datumWriter = new SpecificDatumWriter[S#J](m.avroClass)
      val datumFileWriter = new DataFileWriter[S#J](datumWriter)

      datumFileWriter.create(m.schema, outs)
      avroItems.foreach(item => datumFileWriter.append(item))
      datumFileWriter.close()
    }

    writeFile()
  }
}

object AvroWriter {
  def apply[S <: AvroSerializeable](file: File) = new AvroWriter[S](file)
  def apply[S <: AvroSerializeable](filename: String) = new AvroWriter[S](new File(filename))
}
