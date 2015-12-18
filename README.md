# Scavro

Scavro is an [SBT](http://www.scala-sbt.org/) plugin for automatically calling
Avro code generation and a thin scala wrapper for reading and writing
[Avro](http://avro.apache.org/) files.

The two components can work fully independently, so one does not need to depend
on the plugin after code has been generated.


## Scavro Plugin

##### Setup

The Scavro Plugin is an SBT plugin that automates calling Avro code
generation.  To use, you must import the scavro library into your project's SBT
settings by adding
`addSbtPlugin("com.oysterbooks" % "scavro_2.10" % "0.9.0-SNAPSHOT")` to your
`plugins.sbt` file.   Avro schema and protocol files can then be added to your
`Build.scala` or `build.sbt` file.

```scala
avroSchemaFiles := Seq(file("SchemaFile.avsc"))
avroProtocolFiles := Seq(file("ProtocolFile.avpr"))
avroIDLFiles := Seq(file("AvroIdlFile.avdl"))
avroDataFiles := Seq(file("AvroDataFile.avro"))
```

##### Generating Code

Running `sbt compile` will then call the avro-tools and avrohugger-tools
compilers, generating java files into the directory specified by the
`avroCodeOutputDirectory` SBT key and scala files into the directory specified
by the `avroScalaCodeOutputDirectory` SBT key.


##### Namespaces

The generated scala wrapper classes cannot occupy the same namespace as their
java counterparts, so  Use the `avroScalaCustomNamespace` SBT key to
override this setting with a custom namespace:


```scala
avroScalaCustomNamespace := Map("com.oysterbooks.scavrodemo.idl"->"com.oysterbooks.scavrodemo.model")

```


#### Scala Types

Currently all avro types are supported except the following: `fixed`, `bytes`.
`array` and number types can be remapped using the `avroScalaCustomTypes` SBT
key:


```scala
avroScalaCustomTypes := Map("array"->classOf[Seq[_]])
```


##### Demo

A complete demonstration project is available as a reference.

    # cd demo
    # sbt compile
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
`AvroSerializeable` trait.  For example, provided that `LineItem.java` and
`LineItem.scala` have been generated, avro datafiles can be written and read
with an `AvroWriter` and `AvroReader`:

```scala
dataToWrite: Seq[LineItem] = ...

// Write an avro file
val writer = AvroWriter[LineItem](filename)
writer.write(dataToWrite)

// Read an avro file
val reader: AvroReader[LineItem] = AvroReader[LineItem]
val dataRead: Seq[LineItem] = reader.read(filename)
```

To utilize the Scavro runtime classes add the following to your `build.sbt` or
`Build.scala` file.  This dependency is independent of the `addSbtPlugin`
command described in the _Scavro Plugin_ section.  Use that if you want the
compile time code generation functionality, use this if you want the runtime
functionality, and use both if you want both.

```scala
libraryDependencies ++= Seq(
      "com.oysterbooks" % "scavro_2.10_0.13" % "0.9.0-SNAPSHOT" from
        "https://oss.sonatype.org/content/repositories/snapshots/com/oysterbooks/" +
          "scavro_2.10_0.13/0.9.0-SNAPSHOT/scavro-0.9.0-SNAPSHOT.jar")
```


## Contributing

Contributions are welcome.  Submit pull requests to the master branch for core
improvements and bug fixes.  If you feel more ambitious, check out the
`code_generation` feature branch.

The demo project is considered an integration test and must run correctly before
pull requests will be accepted.  Run `stb test` in both the project root
directory *and also in the demo directory*.


## Authors

* Brian London <https://twitter.com/brianmlondon>
* Julian Peeters <https://twitter.com/julianpeeters>

Thanks for assistance:

* Thierry Bertin-Mahieux <https://github.com/tbertinmahieux>
* Dhiren Bhatia <https://twitter.com/dhirenb>
* Mengxi Lu <https://twitter.com/mengxilu>

*... and the Oyster engineering team.*


## License

Copyright (C) 2015 Oyster

Licensed under the Apache 2.0 license.
http://www.apache.org/licenses/LICENSE-2.0
