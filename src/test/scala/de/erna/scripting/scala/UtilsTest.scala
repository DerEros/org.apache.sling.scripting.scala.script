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

class UtilsTest extends FunSuite {
  test("Make identifier does not change valid identifies") {
    assertResult("FooBarIdentifier") {
      Utils.makeIdentifier("FooBarIdentifier")
    }
  }

  test("Make identifier prepends invalid start character") {
    assertResult("_1dentifier") {
      Utils.makeIdentifier("1dentifier")
    }
  }

  test("Make identifier replaces dot characters and not numbers inside identifiers") {
    assertResult("Identi_f1er") {
      Utils.makeIdentifier("Identi.f1er")
    }
  }

  test("Make identifier replaces unicode characters with their code") {
    assertResult("Identi_d83d_de09fier") {
      Utils.makeIdentifier("""Identi😉fier""")
    }
  }

  test("Make identifier replaces prepends reserved Java keywords with _") {
    assertResult("void_") {
      Utils.makeIdentifier("void")
    }
  }
}
