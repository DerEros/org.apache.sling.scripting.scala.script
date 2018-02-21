package de.erna.scripting.scala.bundlefs

import java.io.{File, IOException, InputStream}
import java.net.URL

import de.erna.scripting.scala.NonExistingFile
import de.erna.scripting.scala.NonExistingFile
import de.erna.scripting.scala.Utils.valueOrElse
import org.osgi.framework.Bundle
import org.slf4s.Logging

import scala.tools.nsc.io.AbstractFile

abstract class BundleEntry(val bundle: Bundle, val url: URL, parent: DirEntry) extends AbstractFile with Logging {
  require(url != null, "url must not be null")
  lazy val (path: String, name: String) = getPathAndName(url)
  lazy val fullName: String = (path::name::Nil).filter(_.size != 0).mkString("/")

  /**
    * @return null
    */
  def file: File = null

  def absolute = this

  /**
    * @return last modification time or 0 if not known
    */
  def lastModified: Long =
    try { url.openConnection.getLastModified }
    catch { case _: Throwable => 0 }

  @throws(classOf[IOException])
  def container: AbstractFile =
    valueOrElse(parent) {
      throw new IOException("No container")
    }

  @throws(classOf[IOException])
  def input: InputStream = {
    log.debug(s"Accessing stream for $url")
    url.openStream()
  }

  /**
    * Not supported. Always throws an IOException.
    * @throws IOException
    */
  @throws(classOf[IOException])
  def output = throw new IOException("not supported: output")

  def create { unsupported }
  def delete { unsupported }

  def lookupNameUnchecked(name: String, directory: Boolean) = {
    val file = lookupName(name, directory)
    if (file == null) NonExistingFile
    else file
  }

  private def getPathAndName(url: URL): (String, String) = {
    val u = url.getPath
    var k = u.length
    while( (k > 0) && (u(k - 1) == '/') )
      k = k - 1

    var j = k
    while( (j > 0) && (u(j - 1) != '/') )
      j = j - 1

    (u.substring(if (j > 0) 1 else 0, if (j > 1) j - 1 else j), u.substring(j, k))
  }

  override def toString = fullName

  override def toURL = url
}
