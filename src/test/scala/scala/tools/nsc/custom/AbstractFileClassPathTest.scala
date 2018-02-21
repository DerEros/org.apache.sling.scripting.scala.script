package scala.tools.nsc.custom

import org.scalatest.FunSuite

class AbstractFileClassPathTest extends FunSuite {
  test("An abstract file class path fails when querying for a package") {
    val abstractFileClassPath = new AbstractFileClassPath(null)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.hasPackage("foobar")
    }
  }

  test("An abstract file class path fails when listing packages") {
    val abstractFileClassPath = new AbstractFileClassPath(null)
    assertThrows[NotImplementedError] {
      abstractFileClassPath.packages("foobar")
    }
  }
}
