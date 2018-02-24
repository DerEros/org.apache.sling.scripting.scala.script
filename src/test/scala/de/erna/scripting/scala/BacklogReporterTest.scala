package de.erna.scripting.scala

import org.scalatest.FunSuite

import scala.reflect.internal.util.{NoSourceFile, Position}
import scala.tools.nsc.Settings

class BacklogReporterTest extends FunSuite {
  def pos(i: Int) = Position.offset(NoSourceFile, i)
  val noSourceFileName = NoSourceFile.toString()

  test ("Cut of the oldest messages when full") {
    val reporter = new BacklogReporter(new Settings(), 3)
    reporter.display(pos(0), "Msg1", reporter.INFO)
    reporter.display(pos(0), "Msg2", reporter.INFO)
    reporter.display(pos(0), "Msg3", reporter.INFO)
    reporter.display(pos(0), "Msg4", reporter.INFO)

    val expected =
      s"""INFO $noSourceFileName line 0 : Msg2
         |INFO $noSourceFileName line 0 : Msg3
         |INFO $noSourceFileName line 0 : Msg4""".stripMargin
    assertResult(expected) { reporter.toString }
  }

  test ("Displaying prompt does not fail") {
    val repoter = new BacklogReporter(new Settings(), 3)
    repoter.displayPrompt()
    succeed
  }
}
