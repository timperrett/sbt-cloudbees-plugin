libraryDependencies += "net.databinder" %% "posterous-sbt" % "0.2.0"

resolvers += "Web plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion("com.github.siasia" %% "xsbt-web-plugin" % _)
