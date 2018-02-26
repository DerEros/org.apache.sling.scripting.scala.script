package de.erna.scripting.scala.interpreter

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.{Files, StandardCopyOption}

import de.erna.scripting.scala.{AbstractScriptInfo, PrivateContainer, ScalaScriptEngineFactory}
import javax.script.{ScriptContext, SimpleScriptContext}
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.junit.Assert._
import org.hamcrest.CoreMatchers._

import scala.io.Source
import scala.reflect.io.PlainFile

class ScalaInterpreterTest extends FunSuite with MockitoSugar {
  val script42 = scriptAsAbstractFile("/Script.scala")
  val scriptCompileError = scriptAsAbstractFile("/ErronousScript.scala")
  val scriptWithBinding = scriptAsAbstractFile("/ScriptWithBinding.scala")

  def createInterpreter(context: ScriptContext) = (new ScalaScriptEngineFactory()).getScalaInterpreter(context)
  def scriptAsAbstractFile(path: String) = {
    val tempFile = File.createTempFile("test", "scalainterpret")
    Files.copy(getClass.getResourceAsStream(path), tempFile.toPath, StandardCopyOption.REPLACE_EXISTING)

    tempFile.deleteOnExit()
    new PlainFile(tempFile)
  }

  test ("Interprete script from file") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", script42, Bindings())
    assertResult(false) { reporter.hasErrors }
  }

  test ("Interprete script from erronous file") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", scriptCompileError, Bindings())
    assertResult(true) { reporter.hasErrors }
  }

  test ("Interprete script from file with output") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val out = new ByteArrayOutputStream
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", script42, Bindings(), null, out)
    assertResult("42") { out.toString }
  }

  test ("Interprete script from string") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val script = Source.fromResource("Script.scala" ).mkString
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", script, Bindings())
    assertResult(false) { reporter.hasErrors }
  }

  test ("Compile script from file") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    assertResult(false) { scalaInterpreter.compile(script42).hasErrors }
  }

  test ("Compile script from file with error") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    assertResult(true) { scalaInterpreter.compile(scriptCompileError).hasErrors }
  }

  test ("Execute script without streams") {
    val context = new SimpleScriptContext
    val scriptInterpreter = createInterpreter(context)
    val scriptInfo = new AbstractScriptInfo() {}
    val script = Source.fromResource("Script.scala" ).mkString
    val scriptClass = scriptInfo.getScriptClass(script, context)

    assertResult(false) { scriptInterpreter.compile(scriptClass, script, Bindings()).hasErrors }
    assertResult(false) { scriptInterpreter.execute(scriptClass, Bindings()).hasErrors }
  }

  test ("Class does not have PUBLIC modifier") {
    val c = Class.forName("de.erna.scripting.scala.PrivateContainer$PrivateClass")
    assertResult(0) { c.getModifiers & 1 }
  }

  test ("Preprocess with private classes in binding") {
    // Need some reflection magic to get a private class into binding; local Scala private classes
    // always had modifier 1 (= public); check with Class[].getModifiers()
    val bindings = Bindings()
    val derivedClazz = Class.forName("de.erna.scripting.scala.PrivateContainer$PrivateClass")
    val constructor = derivedClazz.getConstructors.head
    constructor.setAccessible(true)
    val derived: AnyRef = constructor.newInstance(new PrivateContainer).asInstanceOf[AnyRef]

    bindings.putValue("someObj", derived)
    val scriptInterpreter = createInterpreter(new SimpleScriptContext)
    val scriptHeader = scriptInterpreter.preProcess("de.erna.scripting.scala.Script", scriptWithBinding.mkString("\n"), bindings)
    val expectedDef = "implicit def de_erna_scripting_scala_PrivateContainer$BaseClass2de_erna_scripting_scala_PrivateContainer$PublicInterface(x: de.erna.scripting.scala.PrivateContainer$BaseClass): de.erna.scripting.scala.PrivateContainer$PublicInterface = x.asInstanceOf[de.erna.scripting.scala.PrivateContainer$PublicInterface]"
    assertThat(scriptHeader, containsString(expectedDef))
  }
}
