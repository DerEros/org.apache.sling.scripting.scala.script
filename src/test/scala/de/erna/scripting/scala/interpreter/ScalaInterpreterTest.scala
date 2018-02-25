package de.erna.scripting.scala.interpreter

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.{Files, StandardCopyOption}

import javax.script.{ScriptContext, SimpleScriptContext}
import de.erna.scripting.scala.{AbstractScriptInfo, ScalaScriptEngineFactory}
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar

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

  trait Interface {
    def name(): String
  }
  class Base {
    def name() = "Base"
  }

  test ("Preprocess with views") {
    class Derived extends Base with Interface {
      override def name() = "Derived"
    }

    val bindings = Bindings()
    bindings.putValue("someObj", new Derived)
    val scriptInterpreter = createInterpreter(new SimpleScriptContext)
    assertResult("Derived") {
      val out = new ByteArrayOutputStream()
      scriptInterpreter.interprete("de.erna.scripting.scala.Script", scriptWithBinding, bindings, null, out)
      out.toString
    }
  }
}
