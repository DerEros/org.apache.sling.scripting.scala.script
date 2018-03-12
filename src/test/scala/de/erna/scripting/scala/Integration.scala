package de.erna.scripting.scala

import javax.inject.Inject
import org.junit.Test
import org.junit.runner.RunWith
import org.ops4j.pax.exam.Configuration
import org.ops4j.pax.exam.CoreOptions.{junitBundles, mavenBundle, options}
import org.ops4j.pax.exam.junit.PaxExam
import org.ops4j.pax.exam.spi.reactors.{ExamReactorStrategy, PerMethod}
import org.osgi.framework.BundleContext
import org.scalatest.junit.JUnitSuite

@RunWith(classOf[PaxExam] )
@ExamReactorStrategy(Array(classOf[PerMethod]))
class Integration extends JUnitSuite {
  @Inject
  var bc: BundleContext = _

  @Configuration
  def config() = {
    System.setProperty("org.ops4j.pax.logging.DefaultServiceLog.level", "ERROR")
    options(
             mavenBundle("de.erna", "osgi-scala-scripting", "0.1.0-SNAPSHOT"),
             mavenBundle("org.scala-lang", "scala-library", "2.12.4"),
             mavenBundle("org.scala-lang", "scala-compiler", "2.12.4"),
             mavenBundle("org.scala-lang", "scala-reflect", "2.12.4"),
             mavenBundle("org.scalatest", "scalatest_2.12", "3.0.5"),
             mavenBundle("org.apache.felix", "org.apache.felix.scr", "2.0.14"),
             junitBundles()
           )
  }

  @Test
  def testBundleContextNotNull(): Unit = {
    assert(bc != null)
  }

}
