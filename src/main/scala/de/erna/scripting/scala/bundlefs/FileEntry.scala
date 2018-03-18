package de.erna.scripting.scala.bundlefs

import java.net.URL

import org.osgi.framework.Bundle

import scala.tools.nsc.io.AbstractFile

class FileEntry(bundle: Bundle, url: URL, parent: DirEntry) extends BundleEntry(bundle, url, parent) {

  /**
    * @return false
    */
  def isDirectory: Boolean = false

  override def sizeOption: Option[Int] = Some(bundle.getEntry(fullName).openConnection().getContentLength)

  def iterator: Iterator[AbstractFile] = Iterator.empty

  def lookupName(name: String, directory: Boolean): AbstractFile = null
}

