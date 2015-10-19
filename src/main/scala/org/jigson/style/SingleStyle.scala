package org.jigson.style

import org.jigson.model.JIGValue
import org.jigson.model.JIGObject
import org.jigson.model.JIGArray
import org.jigson.model.JIGString
import org.jigson.model.JIGNumber
import org.jigson.model.JIGNull
import org.jigson.model.JIGBoolean
import org.jigson.model.JIGNull
import org.jigson.model.JIGNumber

object SingleStyle {
  implicit class JIGToJSON (val v: JIGValue) {
    def toStyle(indent : Int = 0) : String = {
      var indentstr:String = "\n" 
      for (i <- 0 to indent -1) indentstr += "\t"
      
      def recToStyle(currentValue:JIGValue, currentPath:Option[String]):String = {
        val keyString = currentPath match { case Some(path) => path + " = "; case None => "" }
        currentValue match {
          case JIGBoolean(value) => keyString + value.toString()
          case JIGNull() => keyString + "null"
          case JIGNumber(value) => keyString + value.toString()
          case JIGString(value) => keyString + "\"" + value.toString().replace("\"","\\\"") + "\""
          case JIGArray(values) => 
            keyString + {for (arrayValue:JIGValue <- values) yield {
                arrayValue match { 
                  case JIGObject(obj) => s"{$indentstr" + recToStyle(arrayValue, None) + s"$indentstr}$indentstr"
                  case default => recToStyle(default, None)
                }
              }
            }.mkString(s"$indentstr[$indentstr", s" ,$indentstr", s"$indentstr]$indentstr")
          case JIGObject(attrs) => 
            {for ((k:String, theValue:JIGValue) <- attrs) yield
              currentPath match {
                case Some(path) => recToStyle(theValue, Some(path + ".\"" + k + "\""))
                case None => recToStyle(theValue, Some("\"" + k + "\""))
              }
            }.mkString(s"$indentstr")
        }
      }
      recToStyle(v, None)
    }
  }
}