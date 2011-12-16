package cloudbees

import sbt._, Project.Initialize
import com.cloudbees.api.{BeesClient,HashWriteProgress}

object Plugin extends Plugin {
  import CloudBeesKeys._

  object CloudBeesKeys {
    // settings
    val host          = SettingKey[String]("host", "Host URL of the CloudBees API")
    val useDeltaWar   = SettingKey[Boolean]("use-delta-war", "Deploy only a delta-WAR to CloudBees (default: true)")
    val username      = SettingKey[Option[String]]("username", "Your CloudBees username")
    val apiKey        = SettingKey[Option[String]]("api-key", "Your CloudBees API key")
    val apiSecret     = SettingKey[Option[String]]("api-secrect", "Your CloudBees API secret")
    val applicationId = SettingKey[Option[String]]("application-id", "The application identifier of the deploying project")
    // tasks
    val applications  = TaskKey[Unit]("applications")
    //val deploy        = TaskKey[Unit]("deploy")
  }

  val cloudBeesSettings: Seq[Setting[_]] =Seq(
    host := "api.cloudbees.com",
    useDeltaWar := true,
    username := None,
    apiKey := None,
    apiSecret := None,
    applicationId := None,
    applications <<= applicationsTask//,
    //deploy <<= deployTask
  )
  
  import scala.collection.JavaConverters._
  
  /***** tasks ******/
  def applicationsTask: Initialize[Task[Unit]] = (host, apiKey, apiSecret) map { (h,keyOpt,secOpt) =>
    val k = require(keyOpt, apiKey)
    val s = require(secOpt, apiSecret)
    client(h,k,s).applicationList.getApplications.asScala.foreach(
      a => ConsoleLogger().info("+ %s - %s".format(a.getTitle, a.getUrls.head)))
  }
  
  /***** internal *****/
  private def client(host: String, key: String, secret: String): BeesClient =
    new BeesClient("http://%s/api".format(host), key, secret, "xml", "1.0")

  private def require[T](value: Option[String], setting: SettingKey[Option[String]]) =
    value.getOrElse {
      sys.error("%s setting is required".format(setting.key.label))
    }

  // val deployTask =
  
}