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
  def beesApplicationId = ""
  def beesUsername = ""
  def beesPassword = ""
  private val beesApiHost = "api.cloudbees.com"
  
  val BeesDeployDescription = "Deploy your WAR to stax.net with bees-deploy"
  lazy val beesDeploy = beesDeployAction
  protected def beesDeployAction = 
    task(beesDeployTask) dependsOn(`package`) describedAs(BeesDeployDescription)
  
  private def beesDeployTask = {
    val username = 
      prompt("CloudBees Username:", () => beesUsername.isEmpty) getOrElse beesUsername
    val password = 
      prompt("CloudBees Password:", () => beesPassword.isEmpty) getOrElse beesPassword
    val appId = 
      prompt("CloudBees Application ID:", () => beesApplicationId.isEmpty) getOrElse beesApplicationId
    
    if(warPath.exists){
      val appConfig = new AppConfig
      val targetAppId = RunCloudPlugin.targetAppId(username, appId)
      val environment = appConfig.getAppliedEnvironments.toArray.toList.mkString(",")       
       
      log.info("Deploying application '%s' to Run@Cloud".format(appId))
       
      val client = new StaxClient("http://%s/api".format(beesApiHost), username, password, "xml", "0.1")
      client.applicationDeployWar(targetAppId, 
        environment, null, 
        warPath.asFile.getAbsolutePath, 
        null, new HashWriteProgress)
    } else {
      log.error("No WAR file exists to deploy to Run@Cloud")
    }
    None
  }
  
  private def trim(s: Option[String]) = s.getOrElse("")
  private def prompt(withText: String, conditional: () => Boolean): Option[String] = 
    if(conditional()) Some(trim(SimpleReader.readLine("\n"+withText)))
    else None
  
}

object RunCloudPlugin {
  def targetAppId(username: String, appId: String) = appId.split("/").toList match {
    case a :: Nil => username+"/"+a
    case _ => appId
  }
}