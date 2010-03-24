import sbt._

class StaxPlugin(info: ProjectInfo) extends PluginProject(info) 
      with posterous.Publish with AutoCompilerPlugins {
  // repositories
  val staxReleases = "stax-release-repo" at "http://mvn.stax.net/content/repositories/public"
  
  // dependencies
  val staxApiClient = "net.stax" % "stax-api-client" % "1.0.20090908-SNAPSHOT" % "compile"
  val staxAppServer = "net.stax" % "stax-appserver" % "1.0.20090908-SNAPSHOT" % "compile"
  
  // compiller plugins
  val inherit = compilerPlugin("inherit" %% "inherit" % "0.1.2")
  
  val sxr = compilerPlugin("org.scala-tools.sxr" %% "sxr" % "0.2.4")
  override def compileOptions =
      CompileOption("-P:sxr:base-directory:" + mainScalaSourcePath.asFile.getAbsolutePath) ::
      super.compileOptions.toList
  def sxrMainPath = outputPath / "classes.sxr"
  def sxrTestPath = outputPath / "test-classes.sxr"
  def sxrPublishPath = normalizedName / version.toString
  lazy val publishSxr = 
    syncTask(sxrMainPath, sxrPublishPath / "main") dependsOn(
      syncTask(sxrTestPath, sxrPublishPath / "test") dependsOn(testCompile)
    )
  
  // repository config for publishing
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)
}