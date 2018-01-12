package de.erna

import java.io.StringWriter
import javax.script.{ScriptContext, ScriptEngine, ScriptEngineFactory}

import org.apache.sling.scripting.scala.ScalaScriptEngineFactory
import org.osgi.framework.{BundleActivator, BundleContext}
import org.slf4s.Logging

class ScriptingBundleActivator extends BundleActivator with Logging {
  class TestInject(sayWhat: String) {
    def saySomething() = sayWhat;
  }

  def getScriptEngine(): ScriptEngine = {
    import scala.collection.JavaConversions._

    val factory = new ScalaScriptEngineFactory
    factory.getScriptEngine
  }

  override def start(bundleContext: BundleContext): Unit = {
    log.info("Scala Scripting Engine starting")

    val scriptEngine: ScriptEngine = getScriptEngine()

    var code = new StringBuilder()
    code.append("package org.apache.sling.scripting.scala{")
    code.append("\n")
    code.append("class Script(args: ScriptArgs) {")
    code.append("\n")
    code.append("import args._")
    code.append("\n")
    code.append("println(\"output:\" + obj.saySomething()) ")
    code.append("\n")
    code.append("}}")

    val say = "hello"

    val b = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE)
    b.put("obj", new TestInject(say))

    val writer = new StringWriter()
    scriptEngine.getContext().setWriter(writer)

    scriptEngine.eval(code.toString(), b)
    log.info(s"output: $say ;  ${writer.toString.trim()}")
  }

  override def stop(bundleContext: BundleContext): Unit = {
    log.info("Scala Scripting Engine stopping")
  }
}
