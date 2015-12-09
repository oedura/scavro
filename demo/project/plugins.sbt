logLevel := Level.Warn

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("org.oedura" % "scavro-plugin" % "1.0.1-SNAPSHOT")
