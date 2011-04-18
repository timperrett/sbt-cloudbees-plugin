import sbt._

class CloudBeesPlugin(info: ProjectInfo) extends PluginProject(info) with posterous.Publish {
  // repositories
  val specsRepo = "specs.repo" at "http://specs.googlecode.com/svn/maven2/"
  val sonatypeRepo = "sonatype.repo" at "https://oss.sonatype.org/content/groups/public"
  
  // dependencies
  val cbclient = "com.cloudbees" % "cloudbees-api-client-nodeps" % "1.0.0-SNAPSHOT" % "compile"
  
  // testing
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.1" % "test->default"
  
  // repository config for publishing
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
}
