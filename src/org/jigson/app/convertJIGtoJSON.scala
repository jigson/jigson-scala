package org.jigson.app

import org.jigson.parser.JIGReader
import scala.util.Success


object convertJIGtoJSON extends App {
  
import org.jigson.style.JSONStyle._  
  
  require(args.length == 1)
  val filename = args(0)
  JIGReader.readFile(filename) match {
    case Success(obj) => println(obj.toJSON());
    case _ => 
  }
}