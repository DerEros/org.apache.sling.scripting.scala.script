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
package de.erna.scripting.scala.interpreter

import scala.annotation.tailrec
import scala.collection._
import scala.util.Try

/**
  * Bindings of names to Values
  */
trait Bindings extends Map[String, AnyRef] {

  /**
    * Associate a value with a name
    *
    * @param name  Name of the binding
    * @param value Value of the binding
    * @return The value which was previously associated with the
    *         given name or null if none.
    */
  def putValue(name: String, value: AnyRef): AnyRef

  /**
    * Calculates the views of a class.
    * Let <code>clazz</code> be of type <code>Clazz[T]</code> for some type T. Let S be the
    * smallest super type of T which is accessible. Then <code>Class[S]</code> is the head
    * of the result list of classes. Let further be J the set of interface implemented by
    * T which are not implemented by S already. For each interface I in J which has no super
    * type I' of I in J <code>Class[I]</code> is included in the tail of the result list of
    * classes.
    *
    * @param clazz the class whos view to calculate
    * @return a list of Class[_] instances representing the views of <code>clazz</code>.
    */
  def getViews(clazz: Class[_]): List[Class[_]] = {
    @tailrec
    def findLeastAccessibleClass(clazz: Class[_]): Class[_] = {
      if (accessible(clazz)) {
        clazz
      } else {
        findLeastAccessibleClass(clazz.getSuperclass)
      }
    }

    def getInterfacesUpTo(clazz: Class[_], bound: Class[_]) = {
      @tailrec
      def getInterfacesUpTo(intfs: mutable.Set[Class[_]], clazz: Class[_], bound: Class[_]): mutable.Set[Class[_]] =
        if (clazz == bound) {
          intfs
        } else {
          getInterfacesUpTo(intfs ++ clazz.getInterfaces.filter(accessible), clazz.getSuperclass, bound)
        }

      getInterfacesUpTo(mutable.Set.empty, clazz, bound)
    }

    def accessible(clazz: Class[_]) = {
      Try {
        getClass.getClassLoader.loadClass(clazz.getName)
        (clazz.getModifiers & 1) == 1
      } getOrElse false
    }

    val leastAccessibleClass = findLeastAccessibleClass(clazz)
    var interfaces = getInterfacesUpTo(clazz, leastAccessibleClass)
    var interfacesSet = Set.empty ++ interfaces

    while (interfacesSet.nonEmpty) {
      val w = interfacesSet.find(_ => true).get
      val p = w.getInterfaces.filter(accessible)
      interfaces = interfaces -- p
      interfacesSet = interfacesSet - w ++ p
    }

    leastAccessibleClass :: interfaces.toList
  }
}

/**
  * Default implementation of  backed by a mutable Map
  */
private class BindingsWrapper(map: mutable.Map[String, AnyRef]) extends Bindings {
  def +[B >: AnyRef](kv: (String, B)): mutable.Map[String, B] = map + kv

  def -(key: String): mutable.Map[String, AnyRef] = map - key

  override def size: Int = map.size

  override def get(name: String): Option[AnyRef] = map.get(name)

  override def iterator: Iterator[(String, AnyRef)] = map.iterator

  def putValue(name: String, value: AnyRef): AnyRef =
    map.put(name, value) match {
      case Some(a) => a
      case None => null
    }
}

object Bindings {

  import scala.collection.convert.ImplicitConversions._

  def apply(): Bindings = new BindingsWrapper(new mutable.HashMap)

  def apply(map: mutable.Map[String, AnyRef]): Bindings = new BindingsWrapper(map)

  def apply(map: java.util.Map[String, AnyRef]): Bindings = new BindingsWrapper(map)
}




