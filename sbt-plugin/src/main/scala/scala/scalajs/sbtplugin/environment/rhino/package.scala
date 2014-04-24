/*                     __                                               *\
**     ________ ___   / /  ___      __ ____  Scala.js sbt plugin        **
**    / __/ __// _ | / /  / _ | __ / // __/  (c) 2013, LAMP/EPFL        **
**  __\ \/ /__/ __ |/ /__/ __ |/_// /_\ \    http://scala-js.org/       **
** /____/\___/_/ |_/____/_/ | |__/ /____/                               **
**                          |/____/                                     **
\*                                                                      */


package scala.scalajs.sbtplugin.environment

import java.io.File

import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.Context
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Undefined
import org.mozilla.javascript.ScriptableObject

import scala.scalajs.tools.io._

package object rhino {

  implicit class ContextOps(val self: Context) extends AnyVal {
    def evaluateFile(scope: Scriptable, file: VirtualJSFile,
        securityDomain: AnyRef = null): Any = {
      self.evaluateString(scope, file.content, file.name, 1, securityDomain)
    }

    @deprecated("Use the overload with a VirtualJSFile instead", "0.4.2")
    def evaluateFile(scope: Scriptable, file: File,
        securityDomain: AnyRef): Any = {
      evaluateFile(scope, FileVirtualJSFile(file), securityDomain)
    }

    @deprecated("Use the overload with a VirtualJSFile instead", "0.4.2")
    def evaluateFile(scope: Scriptable, file: File): Any = {
      evaluateFile(scope, FileVirtualJSFile(file))
    }
  }

  implicit class ScriptableObjectOps(val self: ScriptableObject) {
    def addFunction(name: String, function: Array[AnyRef] => Unit) = {
      val rhinoFunction =
        new BaseFunction {
          override def call(context: Context, scope: Scriptable,
              thisObj: Scriptable, args: Array[AnyRef]): AnyRef = {
            function(args)
            Undefined.instance
          }
        }

      ScriptableObject.putProperty(self, name, rhinoFunction)
    }
  }
}