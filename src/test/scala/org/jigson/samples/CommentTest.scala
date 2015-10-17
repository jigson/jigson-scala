package org.jigson.samples

import scala.util.Try
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.jigson.model.JIGValue
import scala.util.Success
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CommentTest extends TestParser with Matchers {
  
    testRule[Unit](
      title = "line comment",
      input = """# comment
""",
      rule = _.LineComment.run(),
      fvalidate = (res:Try[Unit]) => ()
        
  )
  
  testRule[Unit](
      title = "block comment",
      input = """/* comment 

comment end
*/
""",
      rule = _.BlockComment.run(),
      fvalidate = (res:Try[Unit]) => ()
        
  )
  
   testRule[JIGValue](
      title = "comment everywhere",
      input = """
b = "this" # Comment
c /* comment 2*/ = /*comment 3*/ "that"
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(attrs) =>  
        case _ => fail()
        
      }
  )
  
}