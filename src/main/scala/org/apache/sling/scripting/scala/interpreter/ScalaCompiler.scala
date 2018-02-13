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
package org.apache.sling.scripting.scala.interpreter

import org.slf4s.Logging

import scala.tools.nsc.classpath.{AggregateClassPath, ZipAndJarClassPathFactory}
import scala.tools.nsc.custom.AbstractFileClassPath
import scala.tools.nsc.io.AbstractFile
import scala.tools.nsc.reporters.Reporter
import scala.tools.nsc.util.ClassPath
import scala.tools.nsc.{Global, Settings}

/**
 * Extended Scala compiler which supports a class path with {@link AbstractFile} entries.
 */
class ScalaCompiler(settings: Settings, reporter: Reporter, classes: Array[AbstractFile])
  extends Global(settings, reporter) with Logging {

  override def classPath: ClassPath = {
    val classPathOrig = super.classPath match {
      case aggregate: AggregateClassPath => aggregate.aggregates
      case c: ClassPath => Seq(c)
      case _ => throw new Exception("Class path is not a class of 'ClassPath'")
    }

    val classPathNew = classPathOrig ++ classes.map(c => new AbstractFileClassPath( c ) ).toList
    val aggregatedClassPath = AggregateClassPath.createAggregate(classPathNew.reverse:_*)

    aggregatedClassPath
  }
}
