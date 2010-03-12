package stax

import sbt._
import com.staxnet.appserver.config.{AppConfig,AppConfigHelper}
import com.staxnet.appserver.utils.ZipHelper
import com.staxnet.appserver.utils.ZipHelper.ZipEntryHandler
import net.stax.api.{HashWriteProgress,StaxClient}
import java.io.{BufferedReader,File,FileInputStream,FileOutputStream,IOException,InputStream,InputStreamReader,PrintStream}
import java.util.zip.{ZipEntry,ZipOutputStream}

trait StaxPlugin extends DefaultWebProject {
  def staxApplicationId: String
  def staxUsername: String = ""
  
  /**
   * Deploy
   */
  
   // description
   val StaxDeployDescription = "Deploy your WAR to stax.net with the stax-deploy"
   // command
   lazy val staxDeploy = staxDeployAction
   // action
   protected def staxDeployAction = 
     task(staxDeployTask) dependsOn(`package`) describedAs(StaxDeployDescription)

   private def getCredentials: (String,String) = {
     val username = if(staxUsername.isEmpty){
       val un = prompt("Stax Username:")
       un
     } else staxUsername
     val password = prompt("Stax Password:")
     (username,password)
   }
   
   private def staxDeployTask = {
     
     // println(warPath)
     val appId = prompt("Stax Application ID:")
     val credentials = getCredentials
     val client = new StaxClient(apiUrl, credentials._1, credentials._2, "xml", "0.1");
     client.applicationDeployWar(appId, 
       this.environment, this.message, 
       this.deployFile.getAbsolutePath(), 
       null, new HashWriteProgress());
     
     
     None
   }
  
  /**
   * Utils
   */
  
  private def trim(s: Option[String]) = s.getOrElse("")
  private def prompt(withText: String): String = 
    trim(SimpleReader.readLine("\n"+withText))
  
  
}
