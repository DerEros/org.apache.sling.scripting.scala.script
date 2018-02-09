package scala.tools.nsc.custom

import java.net.URL

import org.slf4s.Logging

import scala.tools.nsc.classpath.{ClassFileEntry, ClassPathEntries}
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.util.{ClassPath, ClassRepresentation}

class AbstractFileClassPath(classFile: AbstractFile) extends ClassPath with Logging {
  val className: String = {
    val i = classFile.name.lastIndexOf(".")
    classFile.name.substring(0, i)
  }
  val fullName = classFile.toString()

  override private[nsc] def hasPackage( pkg: String ) = false

  override private[nsc] def packages( inPackage: String ) = Seq()

  override private[nsc] def classes( inPackage: String ) = Seq(new ClassFileEntry {
    override def file: AbstractFile = classFile
    override def source: Option[ AbstractFile ] = None
    override def binary: Option[ AbstractFile ] = Some(classFile)
    override def name: String = fullName
  })


  override private[nsc] def sources( inPackage: String ) = Seq()

  override private[ nsc ] def list( inPackage: String ) = {
    ClassPathEntries(Seq(), Seq(new ClassRepresentation {
      override def source: Option[ AbstractFile ] = None
      override def binary: Option[ AbstractFile ] = Some(classFile)
      override def name: String = fullName
    }))
  }

  override def asURLs: Seq[ URL ] = Seq( classFile.toURL )

  override def findClassFile( className: String ): Option[ AbstractFile ] =
    if (className == this.className) Some(classFile) else None

  override def asClassPathStrings: Seq[ String ] = Seq( classFile.path )

  override def asSourcePathString: String = ""
}
