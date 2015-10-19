package org.jigson.parser

import java.io.File
import scala.io.BufferedSource
import scala.io.Source
import scala.util.Try
import org.jigson.model.JIGValue
import org.parboiled2.ParserInput.apply
import java.nio.file.Paths


object Reader {
  
  def readFile(filename:String, root:Option[String] = None):Try[JIGValue] = {
    val sourceName = root match {
      case None => filename
      case Some(rootString) => 
        val filePath = Paths.get(rootString, filename);
        filePath.toString()
    }
    using(Source.fromFile(sourceName)) { 
      (source:BufferedSource) =>
        val currentFile = new File(sourceName);
        parseSource(source, Some(currentFile.getParent())) };
  }
  
  private def parseSource(source:BufferedSource, rootDir:Option[String]):Try[JIGValue] = {
    val str = source.mkString
    new Parser(str, rootDir).parse()
  }
  
  private def using[A <: { def close():Unit }, B](resource:A)(f: A => B): B = 
    try {
      f(resource)
    } finally {
      resource.close()
    }
  
}