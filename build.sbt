sbtPlugin := true

organization := "eu.getintheloop"

name := "sbt-cloudbees-plugin"

version := "0.3.0"

// maven repositories
resolvers ++= Seq(
  "specs.repo" at "http://specs.googlecode.com/svn/maven2/",
  "sonatype.repo" at "https://oss.sonatype.org/content/groups/public",
  "Web plugin repo" at "http://siasia.github.com/maven2"
)

scalacOptions += "-deprecation"

libraryDependencies += "com.cloudbees" % "cloudbees-api-client-nodeps" % "1.0.0-SNAPSHOT" % "compile"

libraryDependencies += "org.scala-tools.testing" % "specs" % "1.6.1" % "test"

libraryDependencies += "com.github.siasia" %% "xsbt-web-plugin" % "0.10.0"

// publishing
publishTo := Some("scalatools.releases" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

posterousEmail := "tperrett@googlemail.com"

posterousPassword := "xxxx"

// add the web plugin
// seq(WebPlugin.webSettings :_*)
