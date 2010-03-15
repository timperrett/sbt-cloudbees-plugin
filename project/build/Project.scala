import sbt._

class StaxPlugin(info: ProjectInfo) extends PluginProject(info){
  // repositories
  val staxReleases = "stax-release-repo" at "http://mvn.stax.net/content/repositories/public"
  
  // dependencies
  val staxApiClient = "net.stax" % "stax-api-client" % "1.0.20090908-SNAPSHOT" % "compile"
  val staxAppServer = "net.stax" % "stax-appserver" % "1.0.20090908-SNAPSHOT" % "compile"
  
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  // Credentials(Path.userHome / ".ivy2" / ".credentials", log)
}