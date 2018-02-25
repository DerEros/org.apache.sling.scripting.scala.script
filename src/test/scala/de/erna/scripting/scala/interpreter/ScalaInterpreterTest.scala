package de.erna.scripting.scala.interpreter

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.{Files, StandardCopyOption}

import javax.script.{ScriptContext, SimpleScriptContext}
import de.erna.scripting.scala.ScalaScriptEngineFactory
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar

import scala.io.Source
import scala.reflect.io.PlainFile

class ScalaInterpreterTest extends FunSuite with MockitoSugar {
  val script42 = scriptAsAbstractFile("/Script.scala")
  val scriptCompileError = scriptAsAbstractFile("/ErronousScript.scala")

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

}
