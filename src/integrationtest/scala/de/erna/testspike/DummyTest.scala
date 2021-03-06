package de.erna.testspike

import javax.inject.Inject
import javax.script.ScriptException

import de.erna.scripting.scala.dummy.DummyService
import de.erna.scripting.scala.service.ScalaScriptService
import org.junit.Assert._
import org.junit.Test
import org.junit.runner.RunWith
import org.ops4j.pax.exam.CoreOptions._
import org.ops4j.pax.exam.junit.PaxExam
import org.ops4j.pax.exam.spi.reactors.{ExamReactorStrategy, PerMethod}
import org.ops4j.pax.exam.{Configuration, Option => ExamOption}

@RunWith(classOf[PaxExam])
@ExamReactorStrategy(Array(classOf[PerMethod]))
class DummyTest {

  @Inject
  var service: DummyService = _

  @Inject
  var scriptService: ScalaScriptService = _

  @Configuration
  def config(): Array[ExamOption] = {
    System.setProperty("org.ops4j.pax.logging.DefaultServiceLog.level", "ERROR")
    options(
      mavenBundle("de.erna", "osgi-scala-scripting", "0.1.0-SNAPSHOT"),
      mavenBundle("org.scala-lang", "scala-library", "2.12.4"),
      mavenBundle("org.scala-lang", "scala-compiler", "2.12.4"),
      mavenBundle("org.scala-lang", "scala-reflect", "2.12.4"),
      mavenBundle("org.apache.felix", "org.apache.felix.scr", "2.0.14"),
      junitBundles(),
      frameworkProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("ERROR"))
  }

  @Test
  def spikeTest(): Unit = {
    assertTrue(true)
  }

  @Test
  def spikeStopTest(): Unit = {
    assertEquals("bar", service.foo())
  }
}
