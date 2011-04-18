package bees

import org.specs._
import java.io.File

class RunCloudPluginSpec extends Specification {
  import RunCloudPlugin._
  
  "RunCloud Plugin" should {
    // "form USER/APP-style app id" in {
    //   targetAppId("ron", "sneakoscope") must_== ("ron/sneakoscope")
    //   targetAppId("ron", "hermione/sneakoscope") must_== ("hermione/sneakoscope")
    //   targetAppId("", "hermione/sneakoscope") must_== ("hermione/sneakoscope")
    // }
    "Prompt for information when Option[String] is None" in {
      val thing: Option[String] = None
      val result = thing orPromtFor("Some meaningful information")
      result.isEmpty must_== false
    }
    "Correctly capture the entered information from the prompt" in {
      val result = (new PromptableOption).orPromtFor("Enter '1234'")
      result.get must_== "1234"
    }
    "Read sucsessfully read the properties file at ${user.home}/.bees/bees.config if it exists" in {
      val config = new File(System.getProperty("user.home"), ".bees/bees.config")
      val value = keyFor("bees.api.key") orElse keyFor("bees.apikey")
      if(config.exists) value.isEmpty must_== false
      else value.isEmpty must_== true
    }
  }
}
