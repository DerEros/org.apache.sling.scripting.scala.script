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
package scala.tools.nsc.custom

import java.net.URL

import de.erna.scripting.scala.bundlefs.{BundleEntry, DirEntry}
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

  override def asURLs: Seq[URL] = Seq(abstractFile.toURL)

  override def findClassFile(className: String): Option[AbstractFile] =
    throw new NotImplementedError(s"Looking for class $className - not implemented")

  override def asClassPathStrings: Seq[String] =
    throw new NotImplementedError("Getting class path strings - not implemented")

  override def asSourcePathString: String =
    throw new NotImplementedError("Getting source path as string - not implemented")

  override private[nsc] def hasPackage(pkg: String) =
    throw new NotImplementedError(s"Looking for packages $pkg - not implemented")

  override private[nsc] def packages(inPackage: String) =
    throw new NotImplementedError(s"Looking for packages in $inPackage - not implemented")

  override private[nsc] def classes(inPackage: String) =
    throw new NotImplementedError(s"Looking for classes in $inPackage - not implemented")

  override private[nsc] def sources(inPackage: String) =
    throw new NotImplementedError(s"Looking for sources in $inPackage - not implemented")

  override private[nsc] def list(inPackage: String) = {
    log.trace(s"Listing $inPackage ${bundleString.map("in " + _).getOrElse("")}")

    val path = inPackage.replaceAll("\\.", "/")
    abstractFile.lookupName(path, directory = true) match {
      case dirEntry: DirEntry =>
        log.debug(s"Found $inPackage in ${abstractFile.name}")
        list(inPackage, dirEntry)
      case _ => ClassPathEntries(Seq(), Seq())
    }
  }

  def list(inPackage: String, dirEntry: DirEntry): ClassPathEntries = {
    val prefix = if (inPackage.isEmpty) "" else s"$inPackage."
    val packages = dirEntry.filter(_.isClassContainer).map(f => PackageEntryImpl(s"$prefix${f.name}"))
    val classes = dirEntry.filter(!_.isDirectory).map(ClassFileEntryImpl)

    if (packages.nonEmpty || classes.nonEmpty) {
      log
        .trace(s"Found ${bundleString.map("in " + _).getOrElse("")}: ${packages.mkString(", ")}; ${
          classes
            .mkString(", ")
        }")
    }

    ClassPathEntries(packages.toSeq, classes.toSeq)
  }
}
