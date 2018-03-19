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
package scala.tools.nsc.custom

import org.scalatest.FunSuite

import scala.reflect.internal.util.NoFile

class AbstractFileClassPathTest extends FunSuite {
  test("An abstract file class path fails when querying for a package") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.hasPackage("foobar")
    }
  }

  test("An abstract file class path fails when listing packages") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.packages("foobar")
    }
  }

  test("An abstract file class path fails when listing classes") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.classes("foobar")
    }
  }

  test("An abstract file class path fails when listing sources") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.sources("foobar")
    }
  }

  test("An abstract file class path returns its initial argument as URL") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    val expected = Seq(NoFile.toURL)
    assertResult(expected) {
      abstractFileClassPath.asURLs
    }
  }

  test("An abstract file class path fails when trying to find a class file") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.findClassFile("foobar")
    }
  }

  test("An abstract file class path fails when trying to get the classpath as a string") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.asClassPathStrings
    }
  }

  test("An abstract file class path fails when trying to get the sourcepath as a string") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.asSourcePathString
    }
  }
}
