package de.erna.scripting.scala

import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.tools.nsc.Settings

class AbstractSettingsProviderTest extends FunSuite with BeforeAndAfter {
  var settingsProvider: AbstractSettingsProvider = _

  before {
    settingsProvider = new AbstractSettingsProvider {}
  }

  test("Should throw when null Scala settings are provided") {
    assertThrows[IllegalArgumentException] {
      settingsProvider.setScalaSettings(null)
    }
  }

  test("Should return false when setting Scala settings that were already set before") {
    val settings = new Settings
    settingsProvider.setScalaSettings(settings)
    assertResult(false) {
      settingsProvider.setScalaSettings(settings)
    }
  }

  test("Should throw when null reporter are provided") {
    assertThrows[IllegalArgumentException] {
      settingsProvider.setReporter(null)
    }
  }

  test("Should return true when setting reporter that was already set before") {
    val repoter = new BacklogReporter(new Settings, 10)
    settingsProvider.setReporter(repoter)
    assertResult(false) {
      settingsProvider.setReporter(repoter)
    }
  }
}
