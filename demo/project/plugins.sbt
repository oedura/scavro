logLevel := Level.Warn

//resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".ivy2/local"
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("org.oedura" % "scavro" % "0.9.1-SNAPSHOT")
