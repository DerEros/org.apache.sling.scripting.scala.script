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
package de.erna.scripting.scala.bundlefs

import java.net.URL
import java.util

import org.junit.Assert._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.osgi.framework.Bundle
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Assertion, BeforeAndAfter, FunSuite}

class DirEntryTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  val baseUrl = new URL("file://123/")
  var bundleMock: Bundle = _
  var parentMock: DirEntry = _

  before {
    bundleMock = mock[Bundle]
    parentMock = mock[DirEntry]
  }

  def findSubItemTest[ExcpectedType <: BundleEntry](isDirectory: Boolean,
                                                    expectedClass: Class[ExcpectedType]): Assertion = {
    val subDirectoryUrl = new URL("file://123/foobar")
    when(bundleMock.getEntry(any(classOf[String]))).thenReturn(subDirectoryUrl)

    val dirEntry = new DirEntry(bundleMock, baseUrl, parentMock)
    val result = dirEntry.lookupName("foobar", isDirectory)

    assertTrue(result.getClass.isAssignableFrom(expectedClass))
    assert(bundleMock == as[ExcpectedType](result).bundle)
    assert(subDirectoryUrl == as[ExcpectedType](result).url)
    assert(dirEntry == as[ExcpectedType](result).container)
  }

  test("Lookup non-existing entry") {
    when(bundleMock.getEntry(any(classOf[String]))).thenReturn(null)

    val dirEntry = new DirEntry(bundleMock, baseUrl, parentMock)
    assertNull(dirEntry.lookupName("foobar", directory = true))
  }

  def as[T](item: AnyRef): T = item.asInstanceOf[T]

  test("Lookup of sub-directory") {
    findSubItemTest(isDirectory = true, classOf[DirEntry])
  }

  test("Lookup of sub-file") {
    findSubItemTest(isDirectory = false, classOf[FileEntry])
  }

  test("Fetch bundle entry only via URL") {
    val entries = new util.Vector[String](util.Arrays.asList("foo", "bar")).elements()
    when(bundleMock.getEntryPaths(any(classOf[String]))).thenReturn(entries)
    when(bundleMock.getResource(any(classOf[String]))).thenReturn(null)

    val dirEntry = new DirEntry(bundleMock, baseUrl, parentMock)
    val iterator = dirEntry.iterator

    val result = iterator.next()
    assertTrue(result.isInstanceOf[DirEntry])
    assert(as[DirEntry](result).url == new URL("file://123/foo"))
    assert(as[DirEntry](result).container == dirEntry)
  }

  test("Fetch bundle entry with empty name") {
    val entries = new util.Vector[String](util.Arrays.asList("", "bar")).elements()
    when(bundleMock.getEntryPaths(any(classOf[String]))).thenReturn(entries)
    when(bundleMock.getResource(any(classOf[String]))).thenReturn(null)

    val dirEntry = new DirEntry(bundleMock, baseUrl, parentMock)
    val iterator = dirEntry.iterator

    val result = iterator.next()
    assertTrue(result.isInstanceOf[DirEntry])
    assert(as[DirEntry](result).url == new URL("file://123/"))
    assert(as[DirEntry](result).container == dirEntry)
  }
}
