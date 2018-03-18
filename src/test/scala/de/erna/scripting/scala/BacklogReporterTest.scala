package de.erna.scripting.scala

import org.scalatest.FunSuite

import scala.reflect.internal.util.{NoSourceFile, Position}
import scala.tools.nsc.Settings

class BacklogReporterTest extends FunSuite {
  val noSourceFileName: String = NoSourceFile.toString()

  def pos(i: Int): Position = Position.offset(NoSourceFile, i)

  test("Cut off the oldest messages when full") {
    val reporter = new BacklogReporter(new Settings(), 3)
    reporter.display(pos(0), "Msg1", reporter.INFO)
    reporter.display(pos(0), "Msg2", reporter.INFO)
    reporter.display(pos(0), "Msg3", reporter.INFO)
    reporter.display(pos(0), "Msg4", reporter.INFO)

    val expected =
      "INFO <no source file> line 0 : Msg2\n" +
      "INFO <no source file> line 0 : Msg3\n" +
      "INFO <no source file> line 0 : Msg4"
    assertResult(expected) {
      reporter.toString
    }
  }

  test("Displaying prompt does not fail") {
    val repoter = new BacklogReporter(new Settings(), 3)
    repoter.displayPrompt()
    succeed
  }
}
