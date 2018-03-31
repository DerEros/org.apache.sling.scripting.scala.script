package de.erna.scripting.scala.interpreter

import de.erna.scripting.scala.integration.TestInject
import org.scalatest.FunSuite

import scala.collection.mutable

class PreprocessorTest extends FunSuite {

  test("testWrap") {
    val expectedResult =
      """class FooClass {
        |println('Hello World')
        |}
        |
        |package org.foo {
        |
        |class FooClassArgs(bindings: de.erna.scripting.scala.interpreter.Bindings) {
        |    lazy val foo = bindings.get("foo").get.asInstanceOf[de.erna.scripting.scala.integration.TestInject]
        |}
        |
        |object FooClassRunner {
        |    def main(bindings: de.erna.scripting.scala.interpreter.Bindings,
        |        stdIn: java.io.InputStream,
        |        stdOut: java.io.OutputStream) {
        |            Console.withIn(stdIn) {
        |                Console.withOut(stdOut) {
        |                    new FooClass (new FooClassArgs(bindings))
        |                    stdOut.flush
        |                }
        |            }
        |        }
        |    }
        |}""".stripMargin
    val bindingsMap = mutable.Map[String, AnyRef]("foo" -> new TestInject("bar"))
    val preprocessor = new DefaultPreprocessor(
      "FooClass",
      "org.foo",
      "class FooClass {\nprintln('Hello World')\n}",
      Bindings(bindingsMap)
    )
    assert(preprocessor.wrap() == expectedResult)
  }

}
