package scala.tools.nsc.custom

import java.net.URL

import org.apache.sling.scripting.scala.bundlefs.{BundleEntry, DirEntry, FileEntry}
import org.osgi.framework.Bundle
import org.slf4s.Logging

import scala.tools.nsc.classpath.{ClassFileEntryImpl, ClassPathEntries, PackageEntryImpl}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util.ClassPath

class AbstractFileClassPath(abstractFile: AbstractFile) extends ClassPath with Logging {
  val bundle: Option[Bundle] = abstractFile match {
    case e: BundleEntry => Some(e.bundle)
    case _ => None
  }

  val bundleName: Option[String] = bundle.map(_.getSymbolicName)
  val bundleVersion: Option[String] = bundle.map(_.getVersion.toString)
  val bundleString: Option[String] = for (name <- bundleName; version <- bundleVersion) yield s"$name ($version)"

  override private[nsc] def hasPackage( pkg: String ) = false

  override private[nsc] def packages( inPackage: String ) = Seq()

  override private[nsc] def classes( inPackage: String ) = {
    log.info(s"Looking for classes in $inPackage")
    Seq()
  }

  override private[nsc] def sources( inPackage: String ) = Seq()


  override private[ nsc ] def list( inPackage: String ) = {
    log.info(s"Listing $inPackage ${bundleString.map("in " + _).getOrElse("")}")

    val path = inPackage.replaceAll("\\.", "/")
    abstractFile.lookupName(path, directory = true ) match {
      case dirEntry: DirEntry => {
        log.info(s"Found $inPackage in ${abstractFile.name}")
        list(inPackage, dirEntry)
      }
      case _ => ClassPathEntries(Seq(), Seq())
    }
  }

  def list( inPackage: String, dirEntry: DirEntry ) = {
    val prefix = if (inPackage.isEmpty) "" else s"$inPackage."
    val all = dirEntry.toList
    val packages = dirEntry.filter(_.isClassContainer).map(f => PackageEntryImpl(s"$prefix${f.name}"))
    val classes = dirEntry.filter(!_.isDirectory).map(ClassFileEntryImpl )

    if (packages.nonEmpty || classes.nonEmpty) {
      log.info(s"Found ${bundleString.map("in " + _).getOrElse("")}: ${packages.mkString(", ")}; ${classes.mkString(", ")}")
    }

    ClassPathEntries(packages.toSeq, classes.toSeq)
  }

  override def asURLs: Seq[ URL ] = Seq( abstractFile.toURL )

  override def findClassFile( className: String ): Option[ AbstractFile ] = {
    log.info(s"Looking for class $className")
    None
  }


  override def asClassPathStrings: Seq[ String ] = {
    log.info("Getting class path strings")
    Seq()
  }

  override def asSourcePathString: String = ""
}
