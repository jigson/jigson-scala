package org.jigson.style

import org.jigson.model.JIGObject
import org.jigson.model.JIGArray
import org.jigson.model.JIGString
import org.jigson.model.JIGNumber
import org.jigson.model.JIGValue
import org.jigson.model.JIGNull
import org.jigson.model.JIGBoolean

object JSONStyle {
  implicit class JIGToJSON (val v: JIGValue) {
    def toStyle(indent : Int = 0) : String = {
      var indentstr:String = "\n" 
      for (i <- 0 to indent -1) indentstr += "\t"
      v match {
        case JIGBoolean(value) => value.toString()
        case JIGNumber(value) => value.toString()
        case JIGString(value) => '"' + value.toString().replace("\"", "\\\"") + '"' 
        case JIGNull() => "null"
        case JIGObject(attrs) => {for ((k:String, v:JIGValue) <- attrs) yield "\"" + k + "\" : " + v.toStyle(indent + 1)}.mkString(s"$indentstr{$indentstr", s" ,$indentstr", s"$indentstr}$indentstr")
        case JIGArray(values) => {for (v:JIGValue <- values) yield " " + v.toStyle(indent + 1)}.mkString(s"$indentstr($indentstr", s" ,$indentstr", s"$indentstr)$indentstr")
      }
    }
  }
  
}
