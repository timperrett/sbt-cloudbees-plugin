package bees

import sbt._
import com.cloudbees.api.{BeesClient,HashWriteProgress}

class PromptableOption(val opt: Option[String]){
  def orPromtFor(withText: String): Option[String] = 
    opt orElse SimpleReader.readLine("\n"+withText+": ").map(_.trim)
}

case class UserSettings(key: String, secret: String)

object RunCloudPlugin extends Plugin {
  private[bees] object Internals {
    
    import java.util.Properties
    import java.io.{File,FileInputStream}
    
    implicit def option2PromtableOption(in: Option[String]): PromptableOption = 
      new PromptableOption(in)

    def targetAppId(username: String, appId: String) = appId.split("/").toList match {
      case a :: Nil => username+"/"+a
      case _ => appId
    }

    def configuration: Option[Properties] = {
      val properties = new Properties
      val config = new File(System.getProperty("user.home"), ".bees/bees.config")

      if(config.exists){
        var fis: FileInputStream = null;
        try {
          fis = new FileInputStream(config)
          properties.load(fis)
          fis.close()
          Some(properties)
        } catch {
          case _ => None
        } finally {
          if(fis != null) try { fis.close() } catch { case _ => }
        }
      } else None
    }

    def keyFor(what: String): Option[String] = (for {
      config <- configuration
    } yield config.getProperty(what)) match {
      case s@Some(value:String) if !value.isEmpty => s
      case Some(null) => None
      case _ => None
    }
    
  }
  import Internals._
  import scala.collection.JavaConverters._
  // actual plugin definition
  
  private val log = ConsoleLogger()
  
  // settings
  private val beesApiHost = "api.cloudbees.com"
  private val beesApiKey = keyFor("bees.api.key") orElse keyFor("bees.apikey")
  private val beesSecret = keyFor("bees.api.secret") orElse keyFor("bees.secret")
  
  val beesApplicationId = SettingKey[String]("bees-application-id", 
    "The CloudBees identifier for the application you wish to deploy")
  val beesUsername = SettingKey[String]("bees-username", "Your CloudBees username")
  val beesShouldDeltaWar = SettingKey[Boolean]("bees-should-delta-war", "Deploy only a delta-WAR to CloudBees (default: true)")
  
  // tasks
  val beesAppList = TaskKey[Unit]("bees-app-list", "List the applications in your Run@Cloud account")
  
  private def beesApplistTask {
    log.info("Applications")
    log.info("============")
    client.foreach { 
      _.applicationList.getApplications.asScala.foreach(
        a => log.info("+ %s - %s".format(a.getTitle, a.getUrls.head)))
    }
  }
  
  // set defaults into the users build.sbt
  val deploymentSettings = Seq(
    beesShouldDeltaWar := true,
    beesAppList := beesApplistTask
  )
  
  private def client: Option[BeesClient] = machineSettings.map(s => 
    new BeesClient("http://%s/api".format(beesApiHost), s.key, s.secret, "xml", "1.0"))
  
  private def machineSettings = for {
    key <- beesApiKey orPromtFor("CloudBees API Key")
    secret <- beesSecret orPromtFor("CloudBees Secret")
  } yield UserSettings(key,secret)
}


// trait RunCloudPlugin extends Plugin {
  // import RunCloudPlugin._
  
  
  // val BeesDeployDescription = "Deploy your WAR to stax.net with bees-deploy"
  //   lazy val beesDeploy = beesDeployAction
  //   protected def beesDeployAction = 
  //     task(beesDeployTask) dependsOn(`package`) describedAs(BeesDeployDescription)
  //   
  //   private def beesDeployTask = {
  //     if(!app.isEmpty){
  //       client.foreach { c => 
  //         if(warPath.exists){
  //           log.info("Deploying application '%s' to Run@Cloud".format(app))
  //           c.applicationDeployWar(
  //             app, null, null, warPath.asFile.getAbsolutePath,
  //             null, beesShouldDeltaWar, new HashWriteProgress)
  //         } else log.error("No WAR file exists to deploy to Run@Cloud")
  //     }}
  //     None
  //   }
  //   
  //   private def app = (for{
  //     uid <- beesUsername orPromtFor("CloudBees Username")
  //     aid <- beesApplicationId orPromtFor("CloudBees Application ID")
  //   } yield targetAppId(uid,aid)).getOrElse("<unknown>")
// }


