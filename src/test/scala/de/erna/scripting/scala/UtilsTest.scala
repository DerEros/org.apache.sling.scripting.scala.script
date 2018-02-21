package de.erna.scripting.scala

import org.scalatest.FunSuite

class UtilsTest extends FunSuite {
  test ("Null is returned as null") {
    assertResult(null) { Utils.nullOrElse(null) { _ => "foo" } }
  }

  test ("When parameter is not null, function is executed") {
    val expected = "foobar"
    assertResult(expected) { Utils.nullOrElse("foo") { _ + "bar" } }
  }

  test ("Value is returned when not null") {
    val expected = "foo"
    assertResult(expected) { Utils.valueOrElse("foo")("bar") }
  }

  test ("Default is returned when null") {
    val expected = "bar"
    assertResult(expected) { Utils.valueOrElse(null.asInstanceOf[String])("bar") }
  }

  test ("Option creates Some when value is present") {
    val expected = Some("foo")
    assertResult(expected) { Utils.option("foo") }
  }

  test ("Option creates None when value is not present") {
    assertResult(None) { Utils.option(null) }
  }

  test ("Make identifier does not change valid identifies") {
    assertResult("FooBarIdentifier") { Utils.makeIdentifier("FooBarIdentifier") }
  }

  test ("Make identifier prepends invalid start character") {
    assertResult("_1dentifier") { Utils.makeIdentifier("1dentifier") }
  }

  test ("Make identifier replaces dot characters and not numbers inside identifiers") {
    assertResult("Identi_f1er") { Utils.makeIdentifier("Identi.f1er") }
  }

  test ("Make identifier replaces unicode characters with their code") {
    assertResult("Identi_d83d_de09fier") { Utils.makeIdentifier("""Identi😉fier""") }
  }

  test ("Make identifier replaces prepends reserved Java keywords with _") {
    assertResult("_void") { Utils.makeIdentifier("void") }
  }
}
