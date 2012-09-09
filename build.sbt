
sbtPlugin := true

organization := "eu.getintheloop"

name := "sbt-cloudbees-plugin"

version := "0.4.2-SNAPSHOT"

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

libraryDependencies <+= sbtVersion(v => v match {
  case "0.11.0" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.0-0.2.8"
  case "0.11.1" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.1-0.2.10"
  case "0.11.2" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"
  case "0.11.3" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.3-0.2.11.1"
  case x if (x.startsWith("0.12")) => "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1"
})

// publishing
publishTo <<= version { (v: String) =>
  if(v endsWith "-SNAPSHOT") Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/")
  else Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials.sonatype")

publishTo <<= version { (v: String) => 
  val nexus = "https://oss.sonatype.org/" 
  if (v.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "content/repositories/snapshots") 
  else Some("releases" at nexus + "service/local/staging/deploy/maven2") 
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { repo => false }

pomExtra := (
  <url>https://github.com/timperrett/sbt-cloudbees-plugin</url>
  <licenses>
    <license>
      <name>Apache 2.0 License</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:timperrett/sbt-cloudbees-plugin.git</url>
    <connection>scm:git:git@github.com:timperrett/sbt-cloudbees-plugin.git</connection>
  </scm>
  <developers>
    <developer>
      <id>timperrett</id>
      <name>Timothy Perrett</name>
      <url>http://timperrett.com</url>
    </developer>
  </developers>)
