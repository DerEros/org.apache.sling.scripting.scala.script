package de.erna.testspike

import de.erna.scripting.scala.service.ScalaScriptService
import javax.inject.Inject
import javax.script.ScriptException
import org.junit.Test
import org.junit.runner.RunWith
import org.ops4j.pax.exam.CoreOptions.{junitBundles, mavenBundle, options}
import org.ops4j.pax.exam.junit.PaxExam
import org.ops4j.pax.exam.spi.reactors.{ExamReactorStrategy, PerMethod}
import org.ops4j.pax.exam.{Configuration, ProbeBuilder, TestProbeBuilder, Option => ExamOption}
import org.osgi.framework.BundleContext

@RunWith(classOf[PaxExam])
@ExamReactorStrategy(Array(classOf[PerMethod]))
class ScalaScriptServiceTest {
  @Inject
  var scalaScriptService: ScalaScriptService = _

  @Inject
  var bc: BundleContext = _

  @Configuration
  def config(): Array[ExamOption] = {
    System.setProperty("org.ops4j.pax.logging.DefaultServiceLog.level", "ERROR")
    options(
      mavenBundle("de.erna", "osgi-scala-scripting", "0.1.0-SNAPSHOT"),
      mavenBundle("org.scala-lang", "scala-library", "2.12.4"),
      mavenBundle("org.scala-lang", "scala-compiler", "2.12.4"),
      mavenBundle("org.scala-lang", "scala-reflect", "2.12.4"),
      mavenBundle("org.apache.felix", "org.apache.felix.scr", "2.0.14"),
      junitBundles()
    )
  }

  @ProbeBuilder
  def probeConfig(testProbeBuilder: TestProbeBuilder): TestProbeBuilder = {
    testProbeBuilder.setHeader("Export-Package", "de;version=1.0.0,de.erna;version=1.0.0,de.erna.testspike;version=1.0.0")
  }

  @Test(expected = classOf[ScriptException])
  def testInvalidScript(): Unit = {
    scalaScriptService.run("foo", Map.empty)
  }

  @Test
  def testSimpleScript(): Unit = {
    val script =
      """
        |package de.erna.scripting.scala {
        |  class Script(args: ScriptArgs) {
        |    import args._
        |
        |    println("Hello Simple Script!")
        |  }
        |}
      """.stripMargin

    scalaScriptService.run(script, Map.empty)
  }

  @Test
  def testScriptWithBinding(): Unit = {
    val script =
      """
        |package de.erna.scripting.scala {
        |
        |  import de.erna.testspike.TestInject
        |
        |  class Script(args: ScriptArgs) {
        |    import args._
        |
        |    println("Hello Script With Binding!")
        |    println(obj.getClass.getCanonicalName)
        |    println(obj.asInstanceOf[TestInject].saySomething())
        |  }
        |}
      """.stripMargin

    val say = "hello"
    val bindings = Map("obj" -> new TestInject(say))

    scalaScriptService.run(script, bindings)
  }
}
