package de.erna.scripting.scala.service

import java.io.StringWriter
import javax.script.{Bindings, ScriptContext, ScriptEngine}

import de.erna.scripting.scala.ScalaScriptEngineFactory
import de.erna.scripting.scala.ScalaScriptEngineFactory.SCALA_CLASSPATH_X
import de.erna.scripting.scala.bundlefs.BundleFS
import org.osgi.framework.{Bundle, BundleContext}
import org.osgi.framework.wiring.BundleWiring
import org.slf4s.Logging
import org.osgi.service.component.ComponentContext
import org.osgi.service.component.annotations.Activate

import scala.tools.nsc.io.AbstractFile

class ScalaScriptServiceImpl extends ScalaScriptService with Logging {
  val scriptEngine: ScriptEngine = getScriptEngine
  val scriptContext: ScriptContext = scriptEngine.getContext
  var componentContext: Option[ComponentContext] = None

  @Activate
  def activate(componentContext: ComponentContext): Unit = {
    this.componentContext = Some(componentContext)
  }

  def getScriptEngine: ScriptEngine = {
    val factory = new ScalaScriptEngineFactory
    factory.getScriptEngine
  }

  def toBinding(source: Map[String, Any]): Bindings = {
    val bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE)
    for ((key, value) <- source) bindings.put(key, value)
    bindings
  }

  def getAllResources(bundleContext: BundleContext): List[AbstractFile] = {
    val fs = BundleFS.create(bundleContext.getBundle)
    val wiring = bundleContext.getBundle.adapt(classOf[BundleWiring])

    val bundles = getWiredBundles(bundleContext).reverse
    val resources = bundles.filter(_.getResource("/") != null).map(BundleFS.create)

    resources
  }

  def getWiredBundles(bundleContext: BundleContext): List[Bundle] = {
    bundleContext.getBundles.toList
  }

  override def run(script: String, sourceBindings: Map[String, Any]): Unit = {
    log.debug("Running script")
    log.trace(s"Script: $script")
    log.trace(s"Bindings: $sourceBindings")

    val bindings = toBinding(sourceBindings)
    val writer = new StringWriter()
    val classes = getAllResources(componentContext.get.getBundleContext)
    scriptContext.setWriter(writer)
    scriptContext.setAttribute(SCALA_CLASSPATH_X, classes.toArray, ScriptContext.ENGINE_SCOPE)

    scriptEngine.eval(script, bindings)

    log.info(s"${writer.toString.trim()}")
  }
}
