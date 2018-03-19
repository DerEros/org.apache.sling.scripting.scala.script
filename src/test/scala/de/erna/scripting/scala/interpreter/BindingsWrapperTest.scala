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

import java.util

import org.junit.Assert._
import org.scalatest.FunSuite

class BindingsWrapperTest extends FunSuite {
  test("Add a binding using operator") {
    val bindings = Bindings()
    var m = bindings + ("foo" -> "bar")
    m = m + ("baz" -> "oof")

    assertEquals(2, m.size)
    assertEquals(Some("bar"), m.get("foo"))
    assertEquals(Some("oof"), m.get("baz"))
  }

  test("Removing a binding using operator") {
    val map = new util.HashMap[String, AnyRef]()
    map.put("foo", "bar")
    map.put("baz", "oof")
    val bindings = Bindings(map)
    val m = bindings - "baz"

    assertEquals(1, m.size)
    assertEquals(Some("bar"), m.get("foo"))
  }

  test("Create using Java Map") {
    val map = new util.HashMap[String, AnyRef]()
    map.put("foo", "bar")
    map.put("baz", "oof")

    val bindings = Bindings(map)

    assertEquals(2, bindings.size)
    assertEquals(Some("bar"), bindings.get("foo"))
    assertEquals(Some("oof"), bindings.get("baz"))
  }
}
