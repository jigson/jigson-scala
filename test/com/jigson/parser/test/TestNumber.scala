package com.jigson.parser.test

import org.scalatest.Matchers
import org.jigson.model.JIGNumber
import scala.util.Success
import org.jigson.model.JIGValue
import org.jigson.model.JIGObject

class TestNumber extends TestParser with Matchers {
  
   testRule[JIGNumber](
      title = "number test : parse 12",
      input = "12",
      rule = _.Number.run(),
      fvalidate = _ should matchPattern { case Success(JIGNumber(12)) => }
  )
  
  testRule[JIGNumber](
      title = "number test : parse 13.5",
      input = "13.5",
      rule = _.Number.run(),
      fvalidate = _ should matchPattern { case Success(JIGNumber(13.5)) => }
  )
  
  testRule[JIGNumber](
      title = "number test : -18.99",
      input = "-18.99",
      rule = _.Number.run(),
      fvalidate = _ should matchPattern { case Success(JIGNumber(-18.99)) => }
  )
  
  testRule[JIGNumber](
      title = "number test : -18.99e15",
      input = "-18.99e15",
      rule = _.Number.run(),
      fvalidate = _ should matchPattern { case Success(JIGNumber(-18.99e15)) => }
  )
 
      testRule[JIGValue](
      title = "arithmetics 1",
      input = """
test1 = 1 + 2
test2 = 3 + 5 * 4
test3 = (3 + 5) * 4

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) => 
          attrs.get("test1") foreach (value => value.toString.toDouble should equal(3))
          attrs.get("test2") foreach (value => value.toString.toDouble should equal(23))
          attrs.get("test3") foreach (value => value.toString.toDouble should equal(32))
        case _ => fail()
        
      }
  )
}