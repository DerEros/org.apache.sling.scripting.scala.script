package de.erna.testspike

import javax.inject.Inject
import javax.script.ScriptException

import de.erna.scripting.scala.service.ScalaScriptService
import org.junit.Test
import org.junit.runner.RunWith
import org.ops4j.pax.exam.{Configuration, Option => ExamOption}
import org.ops4j.pax.exam.CoreOptions.{junitBundles, mavenBundle, options}
import org.ops4j.pax.exam.junit.PaxExam
import org.ops4j.pax.exam.spi.reactors.{ExamReactorStrategy, PerMethod}

@RunWith(classOf[PaxExam])
@ExamReactorStrategy(Array(classOf[PerMethod]))
class ScalaScriptServiceTest {
  @Inject
  var scalaScriptService: ScalaScriptService = _

  @Configuration
  def config(): Array[ExamOption] = options(
    mavenBundle("de.erna", "osgi-scala-scripting", "0.1.0-SNAPSHOT"),
    mavenBundle("org.scala-lang", "scala-library", "2.12.4"),
    mavenBundle("org.scala-lang", "scala-compiler", "2.12.4"),
    mavenBundle("org.scala-lang", "scala-reflect", "2.12.4"),
    mavenBundle("org.apache.felix", "org.apache.felix.scr", "2.0.14"),
    junitBundles())


  @Test(expected = classOf[ScriptException])
  def testInvalidScript(): Unit = {
    scalaScriptService.run("foo", Map.empty)
  }

  @Test
  def testSimpleScript(): Unit = {
    class TestInject(sayWhat: String) {
      def saySomething() = sayWhat
    }

    var code = new StringBuilder()
    code.append("package de.erna.scripting.scala{")
    code.append("\n")
    code.append("class Script(args: ScriptArgs) {")
    code.append("\n")
    code.append("import args._")
    code.append("\n")
    code.append("println(\"output:\" + obj.saySomething()) ")
    code.append("\n")
    code.append("}}")

    val say = "hello"
    val bindings = Map("obj" -> new TestInject(say))

    scalaScriptService.run(code.toString(), bindings)
  }
}
