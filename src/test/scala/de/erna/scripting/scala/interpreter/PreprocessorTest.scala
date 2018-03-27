package de.erna.scripting.scala.interpreter

import de.erna.scripting.scala.integration.TestInject
import org.scalatest.FunSuite

import scala.collection.mutable

class PreprocessorTest extends FunSuite {

  test("testWrap") {
    val bindingsMap = mutable.Map[String, AnyRef]("foo" -> new TestInject("bar"))
    val preprocessor = new DefaultPreprocessor(
      "FooClass",
      "org.foo",
      "class FooClass {\nprintln('Hello World')\n}",
      Bindings(bindingsMap)
    )
    assert(preprocessor.wrap() == "foo\nbar")
  }

}
