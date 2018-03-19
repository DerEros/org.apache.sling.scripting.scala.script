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

