package de.erna.scripting.scala.bundlefs

import java.net.URL

import org.junit.Assert._
import org.osgi.framework.Bundle
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite}

class FileEntryTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  var fileEntry: FileEntry = _

  before {
    fileEntry = new FileEntry(mock[Bundle], new URL("file:///"), mock[DirEntry])
  }

  test("Has no child elements") {
    assertResult(Iterator.empty) {
      fileEntry.iterator
    }
  }

  test("Cannot lookup sub-files") {
    assertNull(fileEntry.lookupName("*", directory = false))
  }

  test("Cannot lookup sub-directories") {
    assertNull(fileEntry.lookupName("*", directory = true))
  }
}
