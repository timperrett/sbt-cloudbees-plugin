package bees

import sbt._
import com.staxnet.appserver._,
  config.{AppConfig,AppConfigHelper},
  utils.ZipHelper,
  utils.ZipHelper.ZipEntryHandler
import net.stax.api.{HashWriteProgress,StaxClient}
import java.io.{BufferedReader,File,FileInputStream,FileOutputStream,
  IOException,InputStream,InputStreamReader,PrintStream}
import java.util.zip.{ZipEntry,ZipOutputStream}

trait RunCloudPlugin extends DefaultWebProject {
  import RunCloudPlugin._
  
  private val beesApiHost = "api.cloudbees.com"
  
  private val beesApiKey: Option[String] = 
    keyFor("bees.api.key") orElse keyFor("bees.apikey")
    
  private val beesSecret: Option[String] =
    keyFor("bees.api.secret") orElse keyFor("bees.secret")
  
  def beesApplicationId: Option[String] = None
  
  val BeesDeployDescription = "Deploy your WAR to stax.net with bees-deploy"
  lazy val beesDeploy = beesDeployAction
  protected def beesDeployAction = 
    task(beesDeployTask) dependsOn(`package`) describedAs(BeesDeployDescription)
  
  private def beesDeployTask: Option[String] = {
    val settings = for {
      key <- beesApiKey orPromtFor("CloudBees API Key")
      secret <- beesSecret orPromtFor("CloudBees Secret")
      appId <- beesApplicationId orPromtFor("CloudBees Application ID")
    } yield UserSettings(key,secret,appId) 
    
    settings.foreach { s =>
      if(warPath.exists){
        log.info("Deploying application '%s' to Run@Cloud".format(s.appId))
        val appConfig = new AppConfig
        val environment = appConfig.getAppliedEnvironments.toArray.toList.mkString(",")
        val client = new StaxClient("http://%s/api".format(beesApiHost), s.key, s.secret, "xml", "0.1")
        client.applicationDeployWar(s.appId, 
          environment, null, warPath.asFile.getAbsolutePath, 
          null, false, new HashWriteProgress)
      } else log.error("No WAR file exists to deploy to Run@Cloud")
    }
    None
  }
}

class PromptableOption {
  def orPromtFor(withText: String): Option[String] = 
    SimpleReader.readLine("\n"+withText+": ").map(_.trim)
}

case class UserSettings(key: String, secret: String, appId: String)

import java.util.Properties
import java.io.{File,FileInputStream}

object RunCloudPlugin {
  
  implicit def option2PromtableOption(in: Option[String]): PromptableOption = 
    new PromptableOption
  
  // def targetAppId(username: String, appId: String) = appId.split("/").toList match {
  //   case a :: Nil => username+"/"+a
  //   case _ => appId
  // }
  
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