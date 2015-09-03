package oyster.scavro

import java.io._

import org.apache.avro.file.{ DataFileReader, SeekableFileInput, SeekableInput }
import org.apache.avro.specific.{ SpecificDatumReader, SpecificRecordBase }

abstract class AvroReader[S <: AvroSerializeable] {
  type J = S#J

  def read(path: String)(implicit m: AvroMetadata[S, J]): Seq[S] = {
    read(new File(path))
  }

  def read(file: File)(implicit m: AvroMetadata[S, J]): Seq[S] = {
    val ins: SeekableInput = new SeekableFileInput(file)
    read(ins)
  }

  def read(ins: SeekableInput)(implicit m: AvroMetadata[S, J]) = {
    val datumReader = new SpecificDatumReader[J](m.avroClass)
    val dataFileReader = new DataFileReader[J](ins, datumReader)

    val sb = Seq.newBuilder[S]
    var lastRead: J = null.asInstanceOf[J]
    while (dataFileReader.hasNext) {
      lastRead = dataFileReader.next(lastRead)
      sb += m.fromAvro(lastRead)
    }
    dataFileReader.close()

    sb.result()
  }
}

object AvroReader {
  def apply[S <: AvroSerializeable](implicit reader: AvroReader[S]) = reader
}
