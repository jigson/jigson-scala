package org.jigson.convert

import org.jigson.parser.Reader
import scala.util.Success


object JIGtoJSON extends App {
  
  import org.jigson.style.JSONStyle._  
  
  require(args.length == 1)
  val filename = args(0)
  Reader.readFile(filename) match {
    case Success(obj) => println(obj.toStyle());
    case _ => 
  }
}