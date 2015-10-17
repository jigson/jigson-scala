package org.jigson.samples

import org.parboiled2.ParseError
import scala.util.Failure
import scala.util.Try
import scala.util.Success
import org.scalatest.FunSuite
import org.jigson.parser.Parser
import java.util.logging.Logging



trait TestParser extends FunSuite {
  
  def testRule[T](title:String, 
                     input:String, 
                     rule:(Parser) => Try[T], 
                     fvalidate:(Try[T]) => Unit) = {
    test(title) {
      
//      info("------------------------------------")
//      info("TEST : " + title)
//      info("INPUT :")
//      info(input)
      val parser = new Parser(input)
      val result = rule(parser);
      result match {
        case Success(obj) => 
//          info("OUTPUT (SUCCESS) :")
//          info(obj.toString())
        case Failure(e:ParseError) =>
          info("OUTPUT (FAILURE) :")
          info(e.format(input))
        case Failure(e) =>
          info("OUTPUT (FAILURE) :")
          e.printStackTrace();
      }
      fvalidate(result)
//      info("------------------------------------")
    }
  }
}