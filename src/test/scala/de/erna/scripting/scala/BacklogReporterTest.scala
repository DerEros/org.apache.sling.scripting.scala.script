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

import org.scalatest.FunSuite

import scala.reflect.internal.util.{NoSourceFile, Position}
import scala.tools.nsc.Settings

class BacklogReporterTest extends FunSuite {
  val noSourceFileName: String = NoSourceFile.toString()

  def pos(i: Int): Position = Position.offset(NoSourceFile, i)

  test("Cut off the oldest messages when full") {
    val reporter = new BacklogReporter(new Settings(), 3)
    reporter.display(pos(0), "Msg1", reporter.INFO)
    reporter.display(pos(0), "Msg2", reporter.INFO)
    reporter.display(pos(0), "Msg3", reporter.INFO)
    reporter.display(pos(0), "Msg4", reporter.INFO)

    val expected =
      "INFO <no source file> line 0 : Msg2\n" +
      "INFO <no source file> line 0 : Msg3\n" +
      "INFO <no source file> line 0 : Msg4"
    assertResult(expected) {
      reporter.toString
    }
  }

  test("Displaying prompt does not fail") {
    val repoter = new BacklogReporter(new Settings(), 3)
    repoter.displayPrompt()
    succeed
  }
}
