package de.erna.scripting.scala.dummy

import org.slf4s.Logging

class DummyServiceImpl extends DummyService with Logging {
  override def foo(): String = {
    log.info("DummyServiceImpl.foo")
    "bar"
  }
}
