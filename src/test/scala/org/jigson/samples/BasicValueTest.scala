package org.jigson.samples

import org.scalatest.Matchers
import scala.util.Failure
import scala.util.Success
import org.jigson.model.JIGNull
import org.jigson.model.JIGBoolean
import org.jigson.model.JIGValue
import org.jigson.model.JIGObject

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BasicValueTest extends TestParser with Matchers {
  
    testRule[JIGValue](
      title = "boolean parsing : 'true'",
      input = "test = true",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        case Success(obj) => obj.as[JIGObject].get[JIGBoolean]("test").value should equal (true) 
        case _ => fail()
      }
        
  )
 
  
  testRule[JIGValue](
      title = "boolean parsing : 'false'",
      input = "test = false",
      rule = _.Input.run(),
      fvalidate = 
      _ match {
        case Success(obj) => obj.as[JIGObject].get[JIGBoolean]("test").value should equal (false) 
        case _ => fail()
      }
  )
  
  testRule[JIGBoolean](
      title = "boolean parsing fail : 'ert'",
      input = "ert",
      rule = _.JBoolean.run(),
      fvalidate = _ should matchPattern {case Failure(e) => }
  )
  
  testRule[JIGNull](
      title = "null parsing",
      input = "null",
      rule = _.Null.run(),
      fvalidate = _ should matchPattern {case Success(JIGNull()) => }
  )

  
}