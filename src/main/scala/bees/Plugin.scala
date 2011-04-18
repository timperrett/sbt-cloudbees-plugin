package bees

import sbt._
import com.cloudbees.api.{BeesClient,HashWriteProgress}

trait RunCloudPlugin extends DefaultWebProject {
  import RunCloudPlugin._
  
  private val beesApiHost = "api.cloudbees.com"
  
  private val beesApiKey = 
    keyFor("bees.api.key") orElse keyFor("bees.apikey")
    
  private val beesSecret =
    keyFor("bees.api.secret") orElse keyFor("bees.secret")
  
  def beesApplicationId: Option[String] = None
  
  val BeesDeployDescription = "Deploy your WAR to stax.net with bees-deploy"
  lazy val beesDeploy = beesDeployAction
  protected def beesDeployAction = 
    task(beesDeployTask) dependsOn(`package`) describedAs(BeesDeployDescription)
  
  private def beesDeployTask: Option[String] = {
    (for {
      key <- beesApiKey orPromtFor("CloudBees API Key")
      secret <- beesSecret orPromtFor("CloudBees Secret")
      appId <- beesApplicationId orPromtFor("CloudBees Application ID")
    } yield UserSettings(key,secret,appId)).foreach { s =>
      if(warPath.exists){
        log.info("Deploying application '%s' to Run@Cloud".format(s.appId))
        val client = new BeesClient("http://%s/api".format(beesApiHost), s.key, s.secret, "xml", "0.1")
        
        client.applicationDeployWar(
          s.appId, 
          null, 
          null, 
          warPath.asFile.getAbsolutePath, 
          null, 
          false,
          new HashWriteProgress)
      } else log.error("No WAR file exists to deploy to Run@Cloud")
    }
    None
  }
}

class PromptableOption(val opt: Option[String]){
  def orPromtFor(withText: String): Option[String] = 
    opt orElse SimpleReader.readLine("\n"+withText+": ").map(_.trim)
}

case class UserSettings(key: String, secret: String, appId: String)

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