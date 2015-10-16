package com.jigson.parser.test

import org.scalatest.Matchers
import org.jigson.model.JIGObject
import org.jigson.model.JIGArray
import scala.util.Success
import org.jigson.model.JIGNull
import org.jigson.model.JIGString
import org.jigson.model.JIGBoolean

class TestArray extends TestParser with Matchers {
   testRule[JIGArray](
      title = "simple array",
      input = "( true \"val2\" null)",
      rule = _.Array.run(),
      fvalidate = 
        _ match {
            case Success(obj) => 
              obj.values should have length 3
              obj.values should contain only (JIGBoolean(true), JIGString("val2"), JIGNull())
            case _ => fail()
          }
  )
  
  testRule[JIGArray](
      title = "simple array 2",
      input = "( true, \"val2\", null)",
      rule = _.Array.run(),
      fvalidate = 
        _ match {
            case Success(obj) => 
              obj.values should have length 3
              obj.values should contain only (JIGBoolean(true), JIGString("val2"), JIGNull())
            case _ => fail()
          }
  )
  
  testRule[JIGObject](
      title = "complex array object",
      input = """
{ attr1 : true
attr2 : 
(
  "val1", "val2", 
    {
      attrInTab1 : null
    }
)
}
""",
    rule = _.Object.run(),
    fvalidate = 
      _ match {
          case Success(obj) => 
          case _ => fail()
        }
  )

}