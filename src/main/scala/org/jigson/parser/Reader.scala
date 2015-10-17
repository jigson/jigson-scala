package org.jigson.parser

import org.jigson.model.JIGValue
import scala.io.Source
import scala.io.Codec
import java.net.URI
import org.jigson.model.JIGNull
import scala.io.BufferedSource
import scala.util.Try

object Reader {
  
  def readFile(filename:String):Try[JIGValue] = {
    using(Source.fromFile(filename)) { parseSource(_) };
  }
  
  def readRemote(uri:String):Try[JIGValue] = {
    using(Source.fromURI(new URI(uri))(Codec.UTF8)) { parseSource(_) };
  }
  
  private def parseSource(source:BufferedSource):Try[JIGValue] = {
    val str = source.mkString
    new Parser(str).parse()
  }
  
  private def using[A <: { def close():Unit }, B](resource:A)(f: A => B): B = 
    try {
      f(resource)
    } finally {
      resource.close()
    }
  
}