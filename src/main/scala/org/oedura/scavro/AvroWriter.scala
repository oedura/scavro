package org.oedura.scavro

import java.io._

import org.apache.avro.file.DataFileWriter
import org.apache.avro.specific.SpecificDatumWriter


/** [[AvroWriter]] serializes a class that implements the [[AvroSerializeable]] trait and writes
  * to the supplied `OutputStream`.
  * {{{
  * dataToWrite: Seq[MyOutputClass] = ...
  * val writer = AvroWriter[MyOutputClass]("MyOutputFile.avro")
  * writer.write(dataToWrite)
  * }}}
  * @param outs  Output stream to write to
  * @tparam S  Type to be serialized
  */
class AvroWriter[S <: AvroSerializeable](outs: OutputStream) {
  /** Writes the sequence of objects to the writer's `OutputStream` */
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

/** Factory for [[AvroWriter]] instances. */
object AvroWriter {
  /** Creates a new [[AvroWriter]] for the desired `OutputStream` */
  def apply[S <: AvroSerializeable](outs: OutputStream) = new AvroWriter[S](outs)
  /** Creates a new [[AvroWriter]] for the desired `File` */
  def apply[S <: AvroSerializeable](file: File) = new AvroWriter[S](new FileOutputStream(file))
  /** Creates a new [[AvroWriter]] for the desired file name */
  def apply[S <: AvroSerializeable](filename: String) = {
    new AvroWriter[S](new FileOutputStream(new File(filename)))
  }
}
