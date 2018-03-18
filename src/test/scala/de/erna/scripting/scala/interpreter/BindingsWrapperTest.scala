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
