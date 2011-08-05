
sbtPlugin := true

organization := "eu.getintheloop"

name := "sbt-cloudbees-plugin"

version in Posterous := "0.3.1"

version <<= (version, sbtVersion) { (v, sbtv) => v + "_" + sbtv }

// maven repositories
resolvers ++= Seq(
  "specs.repo" at "http://specs.googlecode.com/svn/maven2/",
  "sonatype.repo" at "https://oss.sonatype.org/content/groups/public",
  "Web plugin repo" at "http://siasia.github.com/maven2"
)

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.cloudbees" % "cloudbees-api-client-nodeps" % "1.0.0-SNAPSHOT" % "compile",
  "org.scala-tools.testing" % "specs" % "1.6.1" % "test"
)

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % ("0.1.0-"+v))

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