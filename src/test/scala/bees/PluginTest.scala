package bees

import org.specs._

class RunCloudPluginSpec extends Specification {
  "RunCloud Plugin" should {
    "form USER/APP-style app id" in {
      RunCloudPlugin.targetAppId("ron", "sneakoscope") must_== ("ron/sneakoscope")
      RunCloudPlugin.targetAppId("ron", "hermione/sneakoscope") must_== ("hermione/sneakoscope")
      RunCloudPlugin.targetAppId("", "hermione/sneakoscope") must_== ("hermione/sneakoscope")
    }
  }
}
