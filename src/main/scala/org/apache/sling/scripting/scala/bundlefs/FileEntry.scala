package org.apache.sling.scripting.scala.bundlefs

import java.net.URL

import org.osgi.framework.Bundle

import scala.tools.nsc.io.AbstractFile

/**
  * @author Eros Candelaresi <eros@candelaresi.de>
  * @since 10.02.18 22:28
  */
class FileEntry(bundle: Bundle, url: URL, parent: DirEntry) extends BundleEntry(bundle, url, parent) {

  /**
    * @return false
    */
  def isDirectory: Boolean = false
  override def sizeOption: Option[Int] = Some(bundle.getEntry(fullName).openConnection().getContentLength())
  def iterator: Iterator[AbstractFile] = Iterator.empty
  def lookupName(name: String, directory: Boolean): AbstractFile = null
}

