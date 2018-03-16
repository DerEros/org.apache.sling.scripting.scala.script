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
package de.erna.scripting.scala.interpreter

import java.io.{File, PrintWriter}

import de.erna.scripting.scala.Utils.valueOrElse
import javax.script.ScriptException
import org.junit.Assert.{assertEquals, assertFalse}
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.tools.nsc.io.PlainFile

/**
 * Standard test cases where files are read/written to/from the file system.
 */  
class InterpreterTest extends FunSuite with BeforeAndAfter {
  var interpreterHelper: InterpreterHelper = null;
  
  before {
    val workDir = new PlainFile(new File("target")).subdirectoryNamed("tmp")
    val srcDir = workDir.subdirectoryNamed("src")
    val outDir = workDir.subdirectoryNamed("classes")
    
    interpreterHelper = new InterpreterHelper(srcDir, outDir) {
      override def getClasspath = valueOrElse(System.getProperty("surefire.test.class.path")) {
        super.getClasspath
      }
    }
  }

  test ("Use eval to run a simple script") {
    val code = "package a { class Testi(args: TestiArgs) { print(1 + 2) }}"
    assertEquals("3", interpreterHelper.eval("a.Testi", code, Bindings()))
  }

  test ("Use eval to run failing script") {
    val code = "syntax error"
    try {
      interpreterHelper.eval("a.Testi", code, Bindings())
      fail("Expecting ScriptException")
    }
    catch {
      case _: ScriptException =>  // expected
    }
  }

  test ("Use eval to run script that throws error") {
    val err = "Some error here";
    val code = "package a { class Testi(args: TestiArgs) { throw new Error(\"" + err + "\") }}"
    try {
      interpreterHelper.eval("a.Testi", code, Bindings())
      fail("Expecting Exception")
    }
    catch {
      case e: ScriptException if err == e.getCause.getCause.getMessage => // expected
    }
  }

  test ("Use eval to run script with bindings") {
    val bindings = Bindings()
    val time = java.util.Calendar.getInstance.getTime
    bindings.putValue("msg", "Hello world")
    bindings.putValue("time", time)
    val code = "package a { class Testi(args: TestiArgs) {import args._; print(msg + \": \" + time)}}"
    val result = interpreterHelper.eval("a.Testi", code, bindings)
    assertEquals("Hello world: " + time, result)
  }

  test ("Use compile and execute to run script") {
    val srcDir = interpreterHelper.srcDir
    val interpreter = interpreterHelper.interpreter

    val bindings = Bindings()
    val time = java.util.Calendar.getInstance.getTime
    bindings.putValue("msg", "Hello world")
    bindings.putValue("time", time)

    val code = "package a { class Testi(args: TestiArgs) {import args._; print(msg + \": \" + time)}}"
    val src = srcDir.fileNamed("Testi.scala")
    val writer = new PrintWriter(src.output)
    writer.print(code)
    writer.close

    val out = new java.io.ByteArrayOutputStream
    var result = interpreter.compile("a.Testi", src, bindings)
    if (result.hasErrors) {
      fail(result.toString)
    }

    result = interpreter.execute("a.Testi", bindings, None, Some(out))
    assertFalse(result.hasErrors)

    assertEquals("Hello world: " + time, out.toString)
  }

}