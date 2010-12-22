Integration for SBT that lets you deploy apps to the awesome stax.net cloud

Usage
-----

Define the plugin information in your Plugins.scala

<pre><code>
  class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
    val stax = "eu.getintheloop" % "sbt-stax-plugin" % "0.1.2"
    // repos
    val staxReleases = "stax-release-repo" 
      at "http://mvn.stax.net/content/repositories/public"
  }
</code></pre>
 
Add the stax plugin to your SBT project

<pre><code>
  class LiftTravelProject(info: ProjectInfo) 
      extends DefaultWebProject(info) 
      with stax.StaxPlugin {
        ....
        // stax
        override def staxApplicationId = "whatever"
        override def staxUsername = "youruser"
        // leave out and you'll be prompted at deploy time
        // override def staxPassword = "password"
      }
</code></pre>

Now your all configured and good to go, just 
run the deploy action in SBT console:

<code>
  stax-deploy
</code>


