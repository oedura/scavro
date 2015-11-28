package org.oedura.scavro.plugin

import org.scalatest.FlatSpec


class AvroCodegenPluginSpec extends FlatSpec {
  "AvroCodegenPlugin" should "provide avroCodegenTask" in {
    assertCompiles("AvroCodegenPlugin.autoImport.avroCodegenTask")
  }

  it should "provide baseAvroCodegenSettings" in {
    val settings = AvroCodegenPlugin.projectSettings
    assert(settings.nonEmpty)
  }
}
