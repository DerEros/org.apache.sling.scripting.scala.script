/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
