package de.erna.scripting.scala.bundlefs

import java.net.URL
import java.util

import de.erna.scripting.scala.Utils.nullOrElse
import org.osgi.framework.Bundle

import scala.tools.nsc.io.AbstractFile

class DirEntry(bundle: Bundle, url: URL, parent: DirEntry) extends BundleEntry(bundle, url, parent) {

  /**
    * @return true
    */
  def isDirectory: Boolean = true

  def iterator: Iterator[AbstractFile] = {
    new Iterator[AbstractFile]() {
      val dirs: util.Enumeration[String ] = bundle.getEntryPaths(fullName )

      var nextEntry: BundleEntry = prefetch()

      def hasNext: Boolean = {
        if (nextEntry == null)
          nextEntry = prefetch()

        nextEntry != null
      }

      def next(): BundleEntry = {
        if (hasNext) {
          val entry = nextEntry
          nextEntry = null
          entry
        }
        else {
          throw new NoSuchElementException()
        }
      }

      private def prefetch() = {
        if (dirs != null && dirs.hasMoreElements) {
          val entry = dirs.nextElement
          var entryUrl = bundle.getResource("/" + entry)

          // Bundle.getResource seems to be inconsistent with respect to requiring
          // a trailing slash
          if (entryUrl == null)
            entryUrl = bundle.getResource("/" + removeTralingSlash(entry))

          // If still null OSGi wont let use load that resource for some reason
          if (entryUrl == null) {
            entryUrl = new URL(url.toString + entry)
          }

          if (entry.endsWith(".class"))
            new FileEntry(bundle, entryUrl, DirEntry.this)
          else
            new DirEntry(bundle, entryUrl, DirEntry.this)
        }
        else
          null
      }

      private def removeTralingSlash(s: String): String =
        if (s == null || s.length == 0)
          s
        else if (s.last == '/')
          removeTralingSlash(s.substring(0, s.length - 1))
        else
          s
    }
  }

  def lookupName(name: String, directory: Boolean): AbstractFile = {
    val entry = bundle.getEntry(fullName + "/" + name)
    nullOrElse(entry) { entry =>
      if (directory)
        new DirEntry(bundle, entry, DirEntry.this)
      else
        new FileEntry(bundle, entry, DirEntry.this)
    }
  }

}

