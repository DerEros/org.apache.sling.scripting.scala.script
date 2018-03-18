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

import javax.script.{ScriptContext, ScriptEngine, SimpleScriptContext}
import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.tools.nsc.Settings

class ScalaScriptEngineFactoryTest extends FunSuite with BeforeAndAfter {

  var scriptEngineFactory: ScalaScriptEngineFactory = _

  before {
    scriptEngineFactory = new ScalaScriptEngineFactory
  }

  test("Initialize Scala script engine factory without errors") {
    assertNotNull(scriptEngineFactory)
  }

  test("Creating script engine with empty Scala script engine factory throws exception") {
    assertNotNull(scriptEngineFactory.getScriptEngine)
  }

  test("Scala script engine factory returns correct language name") {
    assertEquals("Scala", scriptEngineFactory.getLanguageName)
  }

  test("Scala script engine factory returns correct language version") {
    assertEquals("2.12.4", scriptEngineFactory.getLanguageVersion())
  }

  test("Scala script engine factory returns correct engine name") {
    assertEquals("Scala Scripting Engine", scriptEngineFactory.getEngineName())
  }

  test("Scala script engine factory returns correct engine version") {
    assertEquals("0.9/scala 2.12.4", scriptEngineFactory.getEngineVersion())
  }

  test("Scala script engine factory returns correct extensions") {
    assertThat(scriptEngineFactory.getExtensions(), hasItems("scala"))
  }

  test("Scala script engine factory returns correct mime types") {
    assertThat(scriptEngineFactory.getMimeTypes(), hasItems("application/x-scala"))
  }

  test("Scala script engine factory returns correct names") {
    assertThat(scriptEngineFactory.getNames(), hasItems("scala"))
  }

  test("Scala script engine factory returns the same values via getParameter") {
    assertEquals(scriptEngineFactory.getEngineName, scriptEngineFactory.getParameter(ScriptEngine.ENGINE))
    assertEquals(scriptEngineFactory.getEngineVersion, scriptEngineFactory.getParameter(ScriptEngine.ENGINE_VERSION))
    assertEquals(scriptEngineFactory.getNames.get(0), scriptEngineFactory.getParameter(ScriptEngine.NAME))
    assertEquals(scriptEngineFactory.getLanguageName, scriptEngineFactory.getParameter(ScriptEngine.LANGUAGE))
    assertEquals(scriptEngineFactory.getLanguageVersion,
      scriptEngineFactory.getParameter(ScriptEngine.LANGUAGE_VERSION))
    assertEquals(scriptEngineFactory.getParameter("threading"), "MULTITHREADED")
  }

  test("Scala script engine factory returns null when asked for unknown parameters") {
    assertNull(scriptEngineFactory.getParameter("foobar"))
  }

  test("Scala script engine factory creates correct no-paramter method call") {
    assertEquals("foo.bar()", scriptEngineFactory.getMethodCallSyntax("foo", "bar"))
  }

  test("Scala script engine factory creates correct one-parameter method call") {
    assertEquals("foo.bar(param1)", scriptEngineFactory.getMethodCallSyntax("foo", "bar", "param1"))
  }

  test("Scala script engine factory creates correct two-parameter method call") {
    assertEquals("foo.bar(p1,p2)", scriptEngineFactory.getMethodCallSyntax("foo", "bar", "p1", "p2"))
  }

  test("Scala script engine factory creates correct output statement") {
    assertEquals("println(\"foo\")", scriptEngineFactory.getOutputStatement("foo"))
  }

  test("Scala script engine factory generates correct bootstrap code") {
    val expectedProgram =
      """package de.erna.scripting.scala {
        |  class Script(args: ScriptArgs) {
        |shoot(rockets)  }
        |}
        |""".stripMargin
    val program = scriptEngineFactory.getProgram("shoot(rockets)")
    assertEquals(expectedProgram, program)
  }

  test("Scala script engine factory generates correct bootstrap code with custom script info") {
    val expectedProgram =
      """package foo.bar {
        |  class FooScript(args: FooScriptArgs) {
        |shoot(rockets)  }
        |}
        |""".stripMargin
    val scriptInfo = new AbstractScriptInfo("foo.bar.FooScript") {}
    scriptEngineFactory.setScriptInfo(scriptInfo)
    val program = scriptEngineFactory.getProgram("shoot(rockets)")
    assertEquals(expectedProgram, program)
  }

  test("Scala script engine factory generates correct bootstrap code with null package") {
    val expectedProgram =
      """package null {
        |  class FooScript(args: FooScriptArgs) {
        |shoot(rockets)  }
        |}
        |""".stripMargin
    val scriptInfo = new AbstractScriptInfo("FooScript") {}
    scriptEngineFactory.setScriptInfo(scriptInfo)
    val program = scriptEngineFactory.getProgram("shoot(rockets)")
    assertEquals(expectedProgram, program)
  }

  test("Scala script engine factory throws when null ScriptInfo is provided") {
    assertThrows[IllegalArgumentException] {
      scriptEngineFactory.setScriptInfo(null)
    }
  }

  test("Scala script engine factory creates a default ScriptInfo when none is provided") {
    assertEquals("de.erna.scripting.scala.Script", scriptEngineFactory.getScriptInfoProvider.getDefaultScriptClass)
  }

  test("Scala script engine factory throws when setting null settings provider") {
    assertThrows[IllegalArgumentException] {
      scriptEngineFactory.setSettingsProvider(null)
    }
  }

  test("Scala script engine factory replaces settings provider") {
    val settingsProvider = new AbstractSettingsProvider {}
    scriptEngineFactory.setSettingsProvider(settingsProvider)
    assertEquals(settingsProvider, scriptEngineFactory.getSettingsProvider)
  }

  test("Scala script engine factory uses defaults to provide interpreter when scala settings differ") {
    val ctx = new SimpleScriptContext
    ctx.setAttribute(ScalaScriptEngineFactory.SCALA_SETTINGS, new Settings(), ScriptContext.ENGINE_SCOPE)

    assertNotNull(scriptEngineFactory.getScalaInterpreter(ctx))
  }

  test("Scala script engine factory uses defaults to provide interpreter when reporter differ") {
    val ctx = new SimpleScriptContext
    ctx.setAttribute(ScalaScriptEngineFactory.SCALA_REPORTER, new LogReporter(null, null), ScriptContext.ENGINE_SCOPE)

    assertNotNull(scriptEngineFactory.getScalaInterpreter(ctx))
  }
}