/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.erna.scripting.scala.service

import java.io.StringWriter

import de.erna.scripting.scala.ScalaScriptEngineFactory
import de.erna.scripting.scala.ScalaScriptEngineFactory.SCALA_CLASSPATH_X
import de.erna.scripting.scala.bundlefs.BundleFS
import javax.script.{Bindings, ScriptContext, ScriptEngine}
import org.osgi.framework.{Bundle, BundleContext}
import org.osgi.service.component.ComponentContext
import org.osgi.service.component.annotations.Activate
import org.slf4s.Logging

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

  def toBinding(source: Map[String, Any]): Bindings = {
    val bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE)
    for ((key, value) <- source) bindings.put(key, value)
    bindings
  }

  def getAllResources(bundleContext: BundleContext): List[AbstractFile] = {
    val bundles = getWiredBundles(bundleContext).reverse
    val resources = bundles.filter(_.getResource("/") != null).map(BundleFS.create)

    resources
  }

  def getWiredBundles(bundleContext: BundleContext): List[Bundle] = {
    bundleContext.getBundles.toList
  }
}
