
sbtPlugin := true

organization := "eu.getintheloop"

name := "sbt-cloudbees-plugin"

version := "0.3.0-SNAPSHOT"

// maven repositories
resolvers ++= Seq(
  "specs.repo" at "http://specs.googlecode.com/svn/maven2/",
  "sonatype.repo" at "https://oss.sonatype.org/content/groups/public",
  "Web plugin repo" at "http://siasia.github.com/maven2"
)

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.cloudbees" % "cloudbees-api-client-nodeps" % "1.0.0-SNAPSHOT" % "compile",
  "com.github.siasia" %% "xsbt-web-plugin" % "0.1-SNAPSHOT" % "compile",
  "org.scala-tools.testing" % "specs" % "1.6.1" % "test"
)

// publishing
publishTo := Some("scalatools.releases" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
