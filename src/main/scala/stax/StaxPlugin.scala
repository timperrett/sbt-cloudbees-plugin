package stax

import sbt._
// import net.staxnet.appserver.config.{AppConfig,AppConfigHelper}
// import net.staxnet.appserver.utils.ZipHelper
// import net.staxnet.appserver.utils.ZipHelper.ZipEntryHandler

// import _root_.net.stax.api.{HashWriteProgress,StaxClient}


import java.io.{BufferedReader,File,FileInputStream,FileOutputStream,IOException,InputStream,InputStreamReader,PrintStream}
import java.util.zip.{ZipEntry,ZipOutputStream}

trait StaxPlugin extends DefaultProject {
  /** 
   * deploy to stax.net
   */
  // description
  val StaxDeployDescription = "Deploy your WAR to stax.net with the stax-deploy"
  // command
  lazy val staxDeploy = staxDeployAction
  // action
  protected def staxDeployAction = task {
      None
    } dependsOn(`package`) describedAs(StaxDeployDescription)
  
}