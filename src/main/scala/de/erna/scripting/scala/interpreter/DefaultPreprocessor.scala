package de.erna.scripting.scala.interpreter

import java.io.StringWriter

import com.github.mustachejava.{DefaultMustacheFactory, Mustache}

/**
  * Collection of default settings and template for general purpose script running
  */
class DefaultPreprocessor(className: String,
                          packageName: String,
                          code: String,
                          bindings: Bindings) extends Preprocessor {
  import scala.collection.JavaConverters._
  val defaultTemplatePath = "DefaultCodeTemplate.mustache"
  val engine: Mustache = new DefaultMustacheFactory().compile(defaultTemplatePath)

  override def wrap(): String = {
    val data = Map(
      "className" -> className,
      "packageName" -> packageName,
      "code" -> code,
      "bindings" -> renderBindings().toList.asJava
    )
    val writer = new StringWriter()
    val out = engine.execute(writer, data.asJava)

    out.toString
  }

  def renderBindings(): Iterable[Binding] = bindings.map(Binding.tupled)
}
