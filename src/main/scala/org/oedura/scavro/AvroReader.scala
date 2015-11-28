package org.oedura.scavro

import java.io._

import org.apache.avro.file.{ DataFileReader, SeekableFileInput, SeekableInput }
import org.apache.avro.specific.SpecificDatumReader


/**
  * Reads a serialized Avro file into the provided class.
  *
  * {{{
  * val reader: AvroReader[MyAvroClass] = AvroReader[MyAvroClass]
  * val dataRead: Seq[MyAvroClass] = reader.read("MyDataFile.avro")
  * }}}
  *
  * @tparam S  Class to be deserialized
  */
abstract class AvroReader[S <: AvroSerializeable] {
  type J = S#J

  /**
    * Reads a sequence of elements from the specified file.
    * @param path  Filename to read
    * @return  Deserialized values
    */
  def read(path: String)(implicit m: AvroMetadata[S, J]): Seq[S] = {
    read(new File(path))
  }


  /**
    * Reads a sequence of elements from the specified file.
    * @param file  `File` to read
    * @return  Deserialized values
    */
  def read(file: File)(implicit m: AvroMetadata[S, J]): Seq[S] = {
    val ins: SeekableInput = new SeekableFileInput(file)
    read(ins)
  }

  /**
    * Reads a sequence of elements from the specified `SeekableInput`
    * @param ins  Seekable input stream
    * @return  Deserialized values
    */
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
  /**
   * Retrieves instance of `AvroReader` for class S
   * @tparam S  Class to serialize which should implement [[AvroSerializeable]]
   */
  def apply[S <: AvroSerializeable](implicit reader: AvroReader[S]) = reader
}
