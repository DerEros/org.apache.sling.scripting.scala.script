package de.erna.scripting.scala

import org.osgi.framework.{BundleActivator, BundleContext}
import org.slf4s.Logging

class ScriptingBundleActivator extends BundleActivator with Logging {
  override def start(bundleContext: BundleContext): Unit = {
    log.info("Scala Scripting Engine starting")
  }

  override def stop(bundleContext: BundleContext): Unit = {
    log.info("Scala Scripting Engine stopping")
  }
}
