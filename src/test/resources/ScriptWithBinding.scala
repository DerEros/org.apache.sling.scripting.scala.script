package de.erna.scripting.scala {
  class Script(args: ScriptArgs) {
    import args._

    print(someObj.getName())
  }
}