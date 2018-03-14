package scala.tools.nsc.custom

import org.scalatest.FunSuite

import scala.reflect.internal.util.NoFile

class AbstractFileClassPathTest extends FunSuite {
  test("An abstract file class path fails when querying for a package") {
    val abstractFileClassPath = new AbstractFileClassPath(NoFile )
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
