# Scavro

Scavro is an [SBT](http://www.scala-sbt.org/) plugin for automatically calling
Avro code generation and a thin scala wrapper for reading and writing
[Avro](http://avro.apache.org/) files.

The two components can work fully independently, so one can use the SBT plugin
to automate Avro's Java code generation and use the default
`SpecificDatumWriter` API supplied by Avro.


## Scavro Plugin

The Scavro Plugin is an SBT plugin that automates calling Avro's code 
generation.  To use, you must import the scavro library into your project's
`plugins.sbt` file.   Avro schema and protocol files can then be added to your
`Build.scala` or `build.sbt` file.

```scala
avroSchemaFiles := Seq(file("SchemaFile.avsc"))
avroProtocolFiles := Seq(file("ProtocolFile.avpr"))
avroIDLFiles := Seq(file("AvroIdlFile.avdl"))
```

Running `sbt compile` will then call the avro-tools compiler and generate java
files into the directory specified by the `avroCodeOutputDirectory` SBT key.

Typically, although by no means necessarily, one would write a scala case class 
wrapper around the java class(es) generated in order to use the scala reader and
writer described below.

A complete demonstration project is available as a reference. 

    # cd demo
    # sbt run
    [info] Running oyster.scavrodemo.ReadWriteDemo
    {"name": "Plywood table top", "price": 13.95, "quantity": 1}
    {"name": "2x4 raw lumber", "price": 2.95, "quantity": 2}
    {"name": "Wood Screws -- 3in No. 10", "price": 0.065, "quantity": 30}
    {"name": "Wood varnish (can)", "price": 3.95, "quantity": 1}
    -------------------------
    The order total comes to $25.75
    [success] Total time: 1 s, completed Sep 2, 2015 12:32:41 PM


## Scavro Reader and Writer

Scavro also provides a lightweight scala wrapper for Avro's read and write
functionality through the `AvroReader` and `AvroWriter` classes.  They can be
used to serialize or deserialize a `Seq` of objects that implements the
`AvroSerializeable` trait.  Additionally, there must be an implicit instance of
`AvroMetadata` to map the scala class to the code generated java class.  This
requirement can be met by using `LineItem` from the demo project as a
boilerplate template.

```scala
case class LineItem(name: String, price: Double, quantity: Int) 
    extends AvroSerializeable {
  // Additional boilerplate omited
}

object LineItem {
  implicit def reader = new AvroReader[LineItem] { override type J = ... }
  implicit val metadata: AvroMetadata[LineItem, JLineItem] = 
    new AvroMetadata[LineItem, JLineItem] { ... }
  }
}

dataToWrite: Seq[LineItem] = ...

// Write an avro file
val writer = AvroWriter[LineItem](filename)
writer.write(dataToWrite)

// Read an avro file
val reader: AvroReader[LineItem] = AvroReader[LineItem]
val dataRead: Seq[LineItem] = reader.read(filename)
```

## Authors

* Brian London <https://twitter.com/brianmlondon>

Thanks for assistance:

* Thierry Bertin-Mahieux <https://github.com/tbertinmahieux>
* Dhiren Bhatia <https://twitter.com/dhirenb>
* Mengxi Lu <https://twitter.com/mengxilu>

*... and the Oyster engineering team.*


## License

Copyright (C) 2015 Oyster

License goes here once released
