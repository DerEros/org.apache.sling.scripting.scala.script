package de.erna.scripting.scala.interpreter

import org.fusesource.scalate.TemplateEngine

/**
  * Collection of default settings and template for general purpose script running
  */
class DefaultPreprocessor(className: String,
                          packageName: String,
                          code: String,
                          bindings: Bindings) extends Preprocessor {
  val defaultTemplatePath = "DefaultCodeTemplate.mustache"
  val engine = new TemplateEngine

  override def wrap(): String = {
    val data = Map(
      "className" -> className,
      "packageName" -> packageName,
      "code" -> code,
      "bindings" -> renderBindings()
    )
    val out = engine.layout(defaultTemplatePath, data)

    out
  }

  def renderBindings(): Iterable[Binding] = bindings.map(Binding.tupled)
}
