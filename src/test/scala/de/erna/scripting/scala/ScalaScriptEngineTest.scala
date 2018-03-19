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

import java.io._

import de.erna.scripting.scala.interpreter.ScriptHelper
import javax.script.{ScriptException, SimpleScriptContext}
import org.hamcrest.CoreMatchers._
import org.hamcrest.MatcherAssert._
import org.scalatest.FunSuite

class ScalaScriptEngineTest extends FunSuite {
  val factory = new ScalaScriptEngineFactory

  test("Should return the factory with which it was created") {
    assertResult(factory) {
      factory.getScriptEngine.getFactory
    }
  }

  test("Should return a valid binding object") {
    val bindings = factory.getScriptEngine.createBindings()
    assertThat(bindings, notNullValue())
  }

  test("Should return a new binding object on each call") {
    val engine = factory.getScriptEngine
    val bindings1 = engine.createBindings()
    val bindings2 = engine.createBindings()

    assertThat(bindings1, not(equalTo(bindings2)))
  }

  test("Should successfully evaluate a script provided via a Reader") {
    val ctx = new SimpleScriptContext
    val reader = new StringReader(ScriptHelper.wrapScript("""print("42")"""))
    val engine = factory.getScriptEngine
    val writer = new StringWriter()

    ctx.setWriter(writer)
    engine.eval(reader, ctx)
    assertResult("42") {
      writer.toString
    }
  }

  test("Should successfully evaluate a script that reads from stdin") {
    val ctx = new SimpleScriptContext
    val script = ScriptHelper.wrapScript("""print(scala.io.StdIn.readLine())""")
    val engine = factory.getScriptEngine
    val writer = new StringWriter()

    ctx.setWriter(writer)
    ctx.setReader(new StringReader("foobar"))
    engine.eval(script, ctx)
    assertResult("foobar") {
      writer.toString
    }
  }

  test("Should wrap a read error in an ScriptException") {
    val brokenReader = new Reader {
      override def read(cbuf: Array[Char], off: Int, len: Int): Int = throw new IOException()

      override def close(): Unit = throw new IOException()
    }
    val ctx = new SimpleScriptContext
    assertThrows[ScriptException] {
      factory.getScriptEngine.eval(brokenReader, ctx)
    }
  }
}
