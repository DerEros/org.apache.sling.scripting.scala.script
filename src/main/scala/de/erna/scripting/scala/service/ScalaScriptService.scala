package de.erna.scripting.scala.service

trait ScalaScriptService {
  def run(script: String, bindings: Map[String, Any])
}
