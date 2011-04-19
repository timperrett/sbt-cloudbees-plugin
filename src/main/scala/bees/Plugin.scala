package bees

import sbt._
import com.cloudbees.api.{BeesClient,HashWriteProgress}
import scala.collection.jcl.Conversions._

trait RunCloudPlugin extends DefaultWebProject {
  import RunCloudPlugin._
  
  private val beesApiHost = "api.cloudbees.com"
  
  private val beesApiKey = 
    keyFor("bees.api.key") orElse keyFor("bees.apikey")
  
  private val beesSecret =
    keyFor("bees.api.secret") orElse keyFor("bees.secret")
  
  def beesApplicationId: Option[String] = None
  def beesUsername: Option[String] = None
  def beesShouldDeltaWar = true
  
  val BeesDeployDescription = "Deploy your WAR to stax.net with bees-deploy"
  lazy val beesDeploy = beesDeployAction
  protected def beesDeployAction = 
    task(beesDeployTask) dependsOn(`package`) describedAs(BeesDeployDescription)
  
  private def beesDeployTask = {
    if(!app.isEmpty){
      client.foreach { c => 
        if(warPath.exists){
          log.info("Deploying application '%s' to Run@Cloud".format(app))
          c.applicationDeployWar(
            app, null, null, warPath.asFile.getAbsolutePath,
            null, beesShouldDeltaWar, new HashWriteProgress)
        } else log.error("No WAR file exists to deploy to Run@Cloud")
    }}
    None
  }
  
  val BeesAppListDescription = "List the applications in your Run@Cloud account"
  lazy val beesApplist = beesApplistAction
  protected def beesApplistAction = 
    task(beesApplistTask)
  private def beesApplistTask = {
    println("Applications")
    println("============")
    client.foreach { cloud => 
      cloud.applicationList.getApplications.foreach(
        a => println("+ %s - %s".format(a.getTitle, a.getUrls.first)))
    }
    None
  }
  
  private def settings = for {
    key <- beesApiKey orPromtFor("CloudBees API Key")
    secret <- beesSecret orPromtFor("CloudBees Secret")
  } yield UserSettings(key,secret)
  
  private def client: Option[BeesClient] = settings.map(s => 
    new BeesClient("http://%s/api".format(beesApiHost), s.key, s.secret, "xml", "1.0"))
  
  private def app = (for{
    uid <- beesUsername orPromtFor("CloudBees Username")
    aid <- beesApplicationId orPromtFor("CloudBees Application ID")
  } yield targetAppId(uid,aid)).getOrElse("<unknown>")
}

class PromptableOption(val opt: Option[String]){
  def orPromtFor(withText: String): Option[String] = 
    opt orElse SimpleReader.readLine("\n"+withText+": ").map(_.trim)
}

case class UserSettings(key: String, secret: String)

import java.util.Properties
import java.io.{File,FileInputStream}

object RunCloudPlugin {
  
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