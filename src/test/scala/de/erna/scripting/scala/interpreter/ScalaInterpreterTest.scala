package de.erna.scripting.scala.interpreter

import java.io.File
import java.nio.file.{Files, StandardCopyOption}

import de.erna.scripting.scala.BacklogReporter
import org.scalatest.FunSuite

import scala.reflect.io.{AbstractFile, PlainFile}
import scala.tools.nsc.Settings

class ScalaInterpreterTest extends FunSuite {
  test ("Execute script from file") {
//    val reporter = new BacklogReporter(new Settings, 10)
//    val scalaInterpreter = new ScalaInterpreter(new Settings, reporter, new Array[AbstractFile](1))
//    var scriptFile: File = new File("foo")
//
//    try {
//      scriptFile = File.createTempFile("test", "scalainterpreter")
//      val abstractFile = new PlainFile(scriptFile)
//
//      Files.copy(this.getClass.getResourceAsStream("/Script.scala"), scriptFile.toPath, StandardCopyOption.REPLACE_EXISTING )
//      scalaInterpreter.interprete("de.erna.scripting.scala.Script", abstractFile, Bindings())
//
//      assertResult(false) { reporter.hasErrors }
//    } finally {
//      scriptFile.delete()
//    }

  }
}
