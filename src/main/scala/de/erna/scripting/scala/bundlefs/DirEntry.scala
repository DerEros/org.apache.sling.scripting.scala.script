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
import java.util

import org.osgi.framework.Bundle

import scala.language.postfixOps
import scala.tools.nsc.io.AbstractFile

class DirEntry(bundle: Bundle, url: URL, parent: DirEntry) extends BundleEntry(bundle, url, parent) {

  def isDirectory: Boolean = true

  def iterator: Iterator[AbstractFile] = {
    new Iterator[AbstractFile]() {
      val dirsOpt: Option[util.Enumeration[String]] = Option(bundle.getEntryPaths(fullName))

      var nextEntry: Option[BundleEntry] = prefetch()

      def hasNext: Boolean = {
        nextEntry = nextEntry.orElse(prefetch())
        nextEntry.isDefined
      }

      def next(): BundleEntry = {
        if (hasNext) {
          val entry = nextEntry.get
          nextEntry = None
          entry
        }
        else {
          throw new NoSuchElementException()
        }
      }

      private def prefetch(): Option[BundleEntry] = {
        for (dirs <- dirsOpt if dirs.hasMoreElements;
             entryUrl <- findEntryUrl(dirs.nextElement())) yield {

          if (isClass(entryUrl.getPath)) {
            new FileEntry(bundle, entryUrl, DirEntry.this)
          } else {
            new DirEntry(bundle, entryUrl, DirEntry.this)
          }
        }
      }

      private def findEntryUrl(entry: String): Option[URL] =
        Option(bundle.getResource("/" + entry))
          .orElse(Option(bundle.getResource("/" + removeTralingSlash(entry))))
          .orElse(Option(new URL(url.toString + entry)))

      private def removeTralingSlash(s: String): String =
        if (s == null || s.length == 0) {
          s
        } else if (s.last == '/') {
          removeTralingSlash(s.substring(0, s.length - 1))
        } else {
          s
        }

      private def isClass(entry: String) = entry.endsWith(".class")
    }
  }

  def lookupName(name: String, directory: Boolean): AbstractFile = {
    val entry = bundle.getEntry(fullName + "/" + name)
    Option(entry).map { entry =>
      if (directory) {
        new DirEntry(bundle, entry, DirEntry.this)
      } else {
        new FileEntry(bundle, entry, DirEntry.this)
      }
    } orNull
  }

}

