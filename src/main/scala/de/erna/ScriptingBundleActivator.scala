package de.erna

import java.io.StringWriter
import javax.script.{ScriptContext, ScriptEngine, ScriptEngineFactory}

import org.apache.sling.scripting.scala.ScalaScriptEngineFactory
import org.apache.sling.scripting.scala.ScalaScriptEngineFactory.SCALA_CLASSPATH_X
import org.apache.sling.scripting.scala.bundlefs.BundleFS
import org.osgi.framework.wiring.BundleWiring
import org.osgi.framework.{Bundle, BundleActivator, BundleContext}
import org.slf4s.Logging

import scala.tools.nsc.io.AbstractFile

class ScriptingBundleActivator extends BundleActivator with Logging {
  class TestInject(sayWhat: String) {
    def saySomething() = sayWhat;
  }

  def getScriptEngine(): ScriptEngine = {
    val factory = new ScalaScriptEngineFactory
    factory.getScriptEngine
  }

  def getWiredBundles(bundleContext: BundleContext): List[Bundle] = {
    bundleContext.getBundles.toList
  }

  def findBundleForResource(resourceName: String, bundles: List[Bundle]): Option[Bundle] = {
    bundles.find(b => b.getEntry(resourceName) != null)
  }

  def getFileForClass(className: String, bundle: Bundle): AbstractFile = {
    val fs = BundleFS.create(bundle)
    val pathParts = className.split("/")
    pathParts.foldLeft(fs)((d, sd) => d.lookupName(sd, !sd.endsWith(".class")))
  }

  override def start(bundleContext: BundleContext): Unit = {
    import scala.languageFeature.implicitConversions
    import scala.collection.JavaConverters._

    log.info("Scala Scripting Engine starting")

    val fs = BundleFS.create(bundleContext.getBundle)
    val wiring = bundleContext.getBundle.adapt(classOf[BundleWiring])
//    val classes = wiring.listResources("/", "*", BundleWiring.LISTRESOURCES_RECURSE)

    val bundles = getWiredBundles(bundleContext).reverse

//    val abstractFileOptions = for (cl <- classes.asScala) yield {
//      val containingBundle = findBundleForResource(cl, bundles)
//      for (bundle <- containingBundle) yield {
//        getFileForClass(cl, bundle)
//      }
//    }
//    val abstractFiles = abstractFileOptions.filter(_.isDefined).map(_.get)

    val abstractFiles = bundles.filter(_.getResource("/") != null).map(BundleFS.create)

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
    scriptEngine.getContext.setAttribute(SCALA_CLASSPATH_X, abstractFiles.toArray, ScriptContext.ENGINE_SCOPE)

    scriptEngine.eval(code.toString(), b)
    log.info(s"output: $say ;  ${writer.toString.trim()}")
  }

  override def stop(bundleContext: BundleContext): Unit = {
    log.info("Scala Scripting Engine stopping")
  }
}
