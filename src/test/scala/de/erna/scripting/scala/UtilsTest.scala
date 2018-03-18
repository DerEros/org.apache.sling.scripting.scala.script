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
