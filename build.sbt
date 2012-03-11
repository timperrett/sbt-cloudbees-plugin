
sbtPlugin := true

organization := "eu.getintheloop"

name := "sbt-cloudbees-plugin"

version := "0.4.1-SNAPSHOT"

// maven repositories
resolvers ++= Seq(
  "specs.repo" at "http://specs.googlecode.com/svn/maven2/",
  "sonatype.repo" at "https://oss.sonatype.org/content/groups/public",
  "web-plugin.repo" at "http://siasia.github.com/maven2"
)

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.cloudbees" % "cloudbees-api-client-nodeps" % "1.1.2" % "compile",
  "edu.stanford.ejalbert" % "BrowserLauncher2" % "1.3" % "compile",
  "org.scala-tools.testing" % "specs" % "1.6.1" % "test"
)

libraryDependencies <+= (sbtVersion) { sv =>
  "com.github.siasia" %% "xsbt-web-plugin" % (sv + "-0.2.10")
}

// publishing
publishTo <<= version { (v: String) =>
  if(v endsWith "-SNAPSHOT") Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/")
  else Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false