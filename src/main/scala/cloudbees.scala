package cloudbees

import sbt._, Keys._, Project.Initialize
import com.cloudbees.api.{BeesClient,HashWriteProgress}
import edu.stanford.ejalbert.{BrowserLauncher => BL}

object BrowserLauncher extends BL()

object Plugin extends Plugin {
  import CloudBees._

  case class Client(host: String, key: Option[String], secret: Option[String]){
    def apply() = {
      val k = require(key, apiKey)
      val s = require(secret, apiSecret)
      new BeesClient("https://%s/api".format(host), k, s, "xml", "1.0")
    }
  }

  object CloudBees {
    // settings
    val host          = SettingKey[String]("cloudbees-host", "Host URL of the CloudBees API")
    val useDeltaWar   = SettingKey[Boolean]("cloudbees-use-delta-war", "Deploy only a delta-WAR to CloudBees (default: true)")
    val openOnUpload  = SettingKey[Boolean]("cloudbees-open-on-upload", "Open the application in your default web browser after upload (default: true)")
    val username      = SettingKey[Option[String]]("cloudbees-username", "Your CloudBees username")
    val apiKey        = SettingKey[Option[String]]("cloudbees-api-key", "Your CloudBees API key")
    val apiSecret     = SettingKey[Option[String]]("cloudbees-api-secrect", "Your CloudBees API secret")
    val applicationId = SettingKey[Option[String]]("cloudbees-application-id", "The application identifier of the deploying project")
    val client        = SettingKey[Client]("cloudbees-client")
    // tasks
    val applications  = TaskKey[Unit]("cloudbees-applications")
    val deploy        = TaskKey[Unit]("cloudbees-deploy")
    val open          = TaskKey[Unit]("cloudbees-open", "Open the application in your default web browser")
  }

  import com.earldouglas.xsbtwebplugin.WarPlugin._
  import com.earldouglas.xsbtwebplugin.PluginKeys.packageWar

  val cloudBeesSettings: Seq[Setting[_]] = Seq(
    host := "api.cloudbees.com",
    useDeltaWar := true,
    openOnUpload := true,
    username := None,
    apiKey := None,
    apiSecret := None,
    applicationId := None,
    client <<= (host, apiKey, apiSecret)(Client),
    applications <<= applicationsTask,
    deploy <<= deployTask,
    open <<= openTask
  ) ++ warSettings
  
  import scala.collection.JavaConverters._
  
  /***** tasks ******/
  def applicationsTask = (client, streams) map { (client,s) =>
    client().applicationList.getApplications.asScala.foreach(
      a => s.log.info("+ %s - %s".format(a.getTitle, a.getUrls.head)))
  }
  def deployTask = ((packageWar in Compile), client, username, applicationId, useDeltaWar, openOnUpload, streams) map {
    (war, client, user, app, delta, open, s) =>
      if (war.exists) {
        val to = targetAppId(require(user, username), require(app, applicationId))
        s.log.info("Deploying application '%s' to Run@Cloud".format(to))
        val result = client().applicationDeployWar(to, null, null, war.asFile.getAbsolutePath, null, true, new HashWriteProgress)
        s.log.info("Application avalible at %s".format(result.getUrl))
        if (open) {
          BrowserLauncher.openURLinBrowser(result.getUrl)
        }
      } else sys.error("There was a problem locating the WAR file for this project")
  }
  // can't figure out a way to do this without mapping
  def openTask = (username, applicationId) map { (user, app) => for {
      u <- user
      a <- app
    } BrowserLauncher.openURLinBrowser("http://" + a + "." + u + ".cloudbees.net")
  }

  /***** internal *****/
  private def targetAppId(username: String, appId: String) = appId.split("/").toList match {
    case a :: Nil => username+"/"+a
    case _ => appId
  }
  private def require[T](value: Option[String], setting: SettingKey[Option[String]]) =
    value.getOrElse {
      sys.error("%s setting is required".format(setting.key.label))
    }
}