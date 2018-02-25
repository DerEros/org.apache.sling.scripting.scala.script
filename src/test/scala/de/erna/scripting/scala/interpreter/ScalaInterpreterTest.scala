package de.erna.scripting.scala.interpreter

import java.io.{ByteArrayOutputStream, File}
import java.nio.file.{Files, StandardCopyOption}
import javax.script.{ScriptContext, SimpleScriptContext}

import de.erna.scripting.scala.ScalaScriptEngineFactory
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar

import scala.reflect.io.PlainFile

class ScalaInterpreterTest extends FunSuite with MockitoSugar {
  val script42 = scriptAsAbstractFile("/Script.scala")
  val scriptCompileError = scriptAsAbstractFile("/ErronousScript.scala")
//
  def createInterpreter(context: ScriptContext) = (new ScalaScriptEngineFactory()).getScalaInterpreter(context)
  def scriptAsAbstractFile(path: String) = {
    val tempFile = File.createTempFile("test", "scalainterpret")
    Files.copy(getClass.getResourceAsStream(path), tempFile.toPath, StandardCopyOption.REPLACE_EXISTING)

    tempFile.deleteOnExit()
    new PlainFile(tempFile)
  }

  test ("Execute script from file") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", script42, Bindings())
    assertResult(false) { reporter.hasErrors }
  }

  test ("Execute script from erronous file") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", scriptCompileError, Bindings())
    assertResult(true) { reporter.hasErrors }
  }

  test ("Execute script with output") {
    val scalaInterpreter = createInterpreter(new SimpleScriptContext)
    val out = new ByteArrayOutputStream
    val reporter = scalaInterpreter.interprete("de.erna.scripting.scala.Script", script42, Bindings(), null, out)
    assertResult("42") { out.toString }
  }
}
