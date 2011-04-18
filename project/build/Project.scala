import sbt._

class CloudBeesPlugin(info: ProjectInfo) extends PluginProject(info) with posterous.Publish {
  // repositories
  val staxReleases = "stax-release-repo" at "http://mvn.stax.net/content/repositories/public"
  val specsRepo = "specs-repo" at "http://specs.googlecode.com/svn/maven2/"
  
  // dependencies
  val cloudbeesApiClient = "net.stax" % "stax-api-client" % "1.0.20090908-SNAPSHOT" % "compile"
  val cloudbeesAppServer = "net.stax" % "stax-appserver" % "1.0.20090908-SNAPSHOT" % "compile"
  
  // testing
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.1" % "test->default"
  
  // repository config for publishing
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
}
