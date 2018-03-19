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

import java.net.{URL, URLConnection, URLStreamHandler}

import org.osgi.framework.Bundle
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.reflect.internal.util.NoFile
import scala.reflect.io.AbstractFile

class BundleEntryTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  val baseUrl = new URL("file://123/")
  var bundleMock: Bundle = _
  var parentMock: DirEntry = _

  def createBundleEntry() = new TestBundleEntry(bundleMock, baseUrl, parentMock)

  class TestBundleEntry(bundle: Bundle, url: URL, parent: DirEntry, val subEntry: AbstractFile = null)
    extends BundleEntry(bundle, url, parent) {

    override def isDirectory: Boolean = false

    override def iterator: Iterator[AbstractFile] = Iterator.empty

    override def lookupName(name: String, directory: Boolean): AbstractFile = subEntry
  }

  before {
    bundleMock = mock[Bundle]
    parentMock = mock[DirEntry]
  }

  test("Returns itself for absolute path") {
    val entry = createBundleEntry()
    assert(entry.absolute == entry)
  }

  test("Returns its own URL") {
    assert(createBundleEntry().toURL == baseUrl)
  }

  test("Last modification time of dummy URL is zero") {
    assert(createBundleEntry().lastModified == 0)
  }

  test("Last modification returns zero when URL does not support operation") {
    class MockUrlConnection(url: URL) extends URLConnection(url) {
      override def connect(): Unit = {}

      override def getLastModified: Long = throw new UnsupportedOperationException
    }
    class MockUrlStreamHandler extends URLStreamHandler {
      override def openConnection(u: URL): URLConnection = {
        new MockUrlConnection(u)
      }
    }

    val mockUrl = new URL("mock", "123", 45, "/foo", new MockUrlStreamHandler())
    val entry = new TestBundleEntry(bundleMock, mockUrl, parentMock)
    assert(entry.lastModified == 0)
  }

  test("Getting output stream is not implemented") {
    assertThrows[UnsupportedOperationException] {
      createBundleEntry().output
    }
  }

  test("Creating file is not implemented") {
    assertThrows[UnsupportedOperationException] {
      createBundleEntry().create()
    }
  }

  test("Deleting file is not implemented") {
    assertThrows[UnsupportedOperationException] {
      createBundleEntry().delete()
    }
  }

  test("Looking up non-existing file") {
    assert(createBundleEntry().lookupNameUnchecked("foobar", directory = false) == NoFile)
  }

  test("Looking up existing file") {
    val subEntry = new TestBundleEntry(bundleMock, new URL("file://123/foobar"), parentMock)
    val entry = new TestBundleEntry(bundleMock, baseUrl, parentMock, subEntry)
    assert(entry.lookupNameUnchecked("foobar", directory = false) == subEntry)
  }

  test("Parse different URLs combinations") {
    case class TestData(input: URL, expectedResult: (String, String))
    val urlsAndExpected: Seq[TestData] =
      TestData(new URL("http://123/foo/bar"), ("foo", "bar")) ::
      TestData(new URL("http://123/bar"), ("", "bar")) ::
      TestData(new URL("http://123/foo/bar/baz"), ("foo/bar", "baz")) ::
      TestData(new URL("http://123/foo/bar/baz/"), ("foo/bar", "baz")) ::
      TestData(new URL("http://123/foo/bar/baz///"), ("foo/bar", "baz")) ::
      TestData(new URL("http://123/"), ("", "")) ::
      TestData(new URL("http://123/foo//bar"), ("foo", "bar")) ::
      Nil
    val bundleEntry = new TestBundleEntry(bundleMock, baseUrl, parentMock)

    for (TestData(url, expectedResult) <- urlsAndExpected) {
      assert(bundleEntry.getPathAndName(url) == expectedResult)
    }
  }
}
