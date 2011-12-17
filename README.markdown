CloudBees Run@Cloud SBT Plugin
------------------------------

Integration for SBT that lets you deploy apps to the CloudBees RUN@Cloud PaaS

Usage
-----

Firstly you need to add the plugin to your ~/.sbt/user.sbt or to your regular project build.sbt. You can do that with the following:

<pre><code>resolvers += "sonatype.repo" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("eu.getintheloop" %% "sbt-cloudbees-plugin" % "0.4.0-SNAPSHOT")
</code></pre>

Don't forget to export the settings so they are included by SBT:

<pre><code>seq(cloudBeesSettings :_*)</code></pre>

With those in place, the next thing you'll need to do is head over to grandcentral.cloudbees.com and pickup your API key and secret. These should look like: 

![Grand Central Keys](https://github.com/timperrett/sbt-cloudbees-plugin/raw/master/notes/img/beehive-keys.jpg)

Take these values and apply them in your user.sbt (or regular build file):

<pre><code>seq(cloudBeesSettings :_*)

CloudBees.apiKey := Some("FXXXXXXXXXXX")

CloudBees.apiSecret := Some("AAAAAAAAAAAAAAAAAAAA=")
</code></pre>

Now your all configured and good to go, there are two commands you can run with this plugin:

* Get a list of your configured applications: <code>cloudbees-applications</code>
* Deploy your application <code>cloudbees-deploy</code>
