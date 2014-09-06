
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

sbtVersion in Global <<= scalaBinaryVersion { scala =>
  scala match {
    case "2.10" => "0.13.5"
    case "2.9.2" => "0.12.4"
  }
}

scalaVersion in Global := "2.10.4"

crossScalaVersions := Seq("2.9.2", "2.10.4")

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "com.cloudbees" % "cloudbees-api-client-nodeps" % "1.1.2" % "compile",
  "edu.stanford.ejalbert" % "BrowserLauncher2" % "1.3" % "compile",
  "org.scala-tools.testing" % "specs" % "1.6.1" % "test"
)

addSbtPlugin("com.earldouglas" % "xsbt-web-plugin" % "0.5.0")

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
