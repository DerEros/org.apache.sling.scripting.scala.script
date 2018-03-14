package de.erna.scripting.scala.interpreter

object ScriptHelper {
  val NL: String = System.getProperty("line.separator" )

  def wrapScript(statements: String*): String = {
    s"""
       |package de.erna.scripting.scala {
       |  class Script(args: ScriptArgs) {
       |    ${statements.mkString(NL)}
       |  }
       |}
     """.stripMargin
  }
}
