package de.erna.scripting.scala.bundlefs

import java.net.URL

import de.erna.scripting.scala.Utils.nullOrElse
import org.osgi.framework.Bundle

import scala.tools.nsc.io.AbstractFile

/**
  * @author Eros Candelaresi <eros@candelaresi.de>
  * @since 10.02.18 22:28
  */
class DirEntry(bundle: Bundle, url: URL, parent: DirEntry) extends BundleEntry(bundle, url, parent) {

  /**
    * @return true
    */
  def isDirectory: Boolean = true

  def iterator: Iterator[AbstractFile] = {
    new Iterator[AbstractFile]() {
      val dirs = bundle.getEntryPaths(fullName)

      var nextEntry = prefetch()

      def hasNext() = {
        if (nextEntry == null)
          nextEntry = prefetch()

        nextEntry != null
      }

      def next() = {
        if (hasNext()) {
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
          val entry = dirs.nextElement.asInstanceOf[String]
          var entryUrl = bundle.getResource("/" + entry)

          // Bundle.getResource seems to be inconsistent with respect to requiring
          // a trailing slash
          if (entryUrl == null)
            entryUrl = bundle.getResource("/" + removeTralingSlash(entry))

          // If still null OSGi wont let use load that resource for some reason
          if (entryUrl == null) {
            null
          }
          else {
            if (entry.endsWith(".class"))
              new FileEntry(bundle, entryUrl, DirEntry.this)
            else
              new DirEntry(bundle, entryUrl, DirEntry.this)
          }
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

