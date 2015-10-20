package org.jigson.samples

import org.scalatest.Matchers
import org.jigson.model.JIGString
import scala.util.Success
import org.jigson.model.JIGValue
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class StringTest extends TestParser with Matchers {
  
    testRule[JIGString](
      title = "String parser : Hello world",
      input = "\"Hello world\"",
      rule = _.JString.run(),
      fvalidate = 
        _ match {
          case Success(JIGString(s)) => 
            s shouldEqual "Hello world"
          case _ => fail()  
        }
  )
  
  testRule[JIGString](
      title = "String parser : Hello escaped t world",
      input = "\"Hello\\t world\"",
      rule = _.JString.run(),
      fvalidate = 
        _ match {
            case Success(JIGString(s)) => 
              s shouldEqual "Hello\t world"
            case _ => fail()
          }
   )
  
  testRule[JIGString](
      title = "String parser : Hello u013F world",
      input = "\"Hello\\u013F world\"",
      rule = _.JString.run(),
      fvalidate = 
        _ match {
            case Success(JIGString(s)) => 
              s shouldEqual "Hello\u013F world"
            case _ => fail()
          }
    
  )

    testRule[JIGValue](
      title = "multiline string",
      input = """
b = "this is a multi
line string
does it works ?"
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(attrs) =>  
        case _ => fail()
        
      }
  )

}