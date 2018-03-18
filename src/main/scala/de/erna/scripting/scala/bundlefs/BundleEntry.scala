package de.erna.scripting.scala.bundlefs

import java.io.{File, IOException, InputStream, OutputStream}
import java.net.URL

import org.osgi.framework.Bundle
import org.slf4s.Logging

import scala.reflect.internal.util.NoFile
import scala.tools.nsc.io.AbstractFile
import scala.util.Try

abstract class BundleEntry(val bundle: Bundle, val url: URL, parent: DirEntry) extends AbstractFile with Logging {
  require(url != null, "url must not be null")
  lazy val (path: String, name: String) = getPathAndName(url)
  lazy val fullName: String = (path :: name :: Nil).filter(_.nonEmpty).mkString("/")

  /**
    * @return null
    */
  def file: File = null

  def absolute: BundleEntry = this

  /**
    * @return last modification time or 0 if not known
    */
  def lastModified: Long = Try(url.openConnection().getLastModified).getOrElse(0)

  @throws(classOf[IOException])
  def container: AbstractFile =
    Option(parent).getOrElse { throw new IOException("No container") }

  @throws(classOf[IOException])
  def input: InputStream = {
    log.debug(s"Accessing stream for $url")
    url.openStream()
  }

  def output: OutputStream = unsupported()

  def create(): Unit = unsupported()

  def delete(): Unit = unsupported()

  def lookupNameUnchecked(name: String, directory: Boolean): AbstractFile =
    Option(lookupName(name, directory)).getOrElse(NoFile)

  override def toString: String = fullName

  override def toURL: URL = url

  def getPathAndName(url: URL): (String, String) = {
    val pathTokens = url.getPath.split("/").toList.filter(_.nonEmpty)

    val name = pathTokens.takeRight(1).mkString("")
    val path = pathTokens.dropRight(1).mkString("/")

    (path, name)
  }
}
