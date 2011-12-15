package cloudbees

import sbt._, Project.Initialize
import com.cloudbees.api.{BeesClient,HashWriteProgress}

object Plugin extends Plugin {
  val CloudBees = config("cloudbees")

  // settings
  val useDeltaWar   = SettingKey[Boolean]("use-delta-war", "Deploy only a delta-WAR to CloudBees (default: true)")
  val host          = SettingKey[String]("host", "Host URL of the CloudBees API")
  val apiKey        = SettingKey[Option[String]]("api-key", "Your CloudBees API key")
  val apiSecret     = SettingKey[Option[String]]("api-secrect", "Your CloudBees API secret")
  val applicationId = SettingKey[Option[String]]("application-id", "The application identifier of the deploying project")
  val username      = SettingKey[Option[String]]("username", "Your CloudBees username")
  // tasks
  val applications  = TaskKey[Unit]("applications")
  val deploy        = TaskKey[Unit]("deploy")
  
  val cloudBeesSettings: Seq[Project.Setting[_]] = 
    inConfig(CloudBees)(Seq(
      host := "api.cloudbees.com",
      useDeltaWar := true,
      username := None,
      apiKey := None,
      apiSecret := None,
      applicationId := None,
      applications <<= applicationsTask//,
      //deploy <<= deployTask
  ))

  import scala.collection.JavaConverters._

  /***** tasks ******/
  val applicationsTask: Initialize[Task[Unit]] = (host, apiKey, apiSecret) map { (h,k,s) =>
    client(h,k,s).foreach {
      _.applicationList.getApplications.asScala.foreach(
        a => ConsoleLogger().info("+ %s - %s".format(a.getTitle, a.getUrls.head)))
    }
  }


  /***** internal *****/
  private def client(host: String, key: Option[String], secret: Option[String]): Option[BeesClient] =
    for {
      k <- key
      s <- secret
    } yield new BeesClient("http://%s/api".format(host), k, s, "xml", "1.0")

  // val deployTask =
//  private def beesApplistTask {
//    log.info("Applications")
//    log.info("============")
//    client.foreach {
//      _.applicationList.getApplications.asScala.foreach(
//        a => log.info("+ %s - %s".format(a.getTitle, a.getUrls.head)))
//    }
//  }
  
  
}