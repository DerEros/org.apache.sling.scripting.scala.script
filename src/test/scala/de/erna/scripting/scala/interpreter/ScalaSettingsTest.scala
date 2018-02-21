package de.erna.scripting.scala.interpreter

import org.scalatest.FunSuite

class ScalaSettingsTest extends FunSuite {
  test("Create empty scala settings instance and parse unknown params") {
    assertResult(List("-foo", "bar")) {
      val settings = new ScalaSettings()
      settings.parse("-foo bar")
    }
  }
}
