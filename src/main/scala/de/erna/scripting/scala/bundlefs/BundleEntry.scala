/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
