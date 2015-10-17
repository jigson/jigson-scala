package com.jigson.parser.test

import org.scalatest.Matchers
import scala.util.Success

class TestKey extends TestParser with Matchers {
 
    
  testRule[List[String]](
      title = "Simple key",
      input = "tutu",
      rule = _.Key.run(),
      fvalidate = 
        _ match {
            case Success(deepAttrs) => 
              deepAttrs should have size 1
              deepAttrs should contain only ("tutu")
            case _ => fail()
          }
  )
  
  testRule[List[String]](
      title = "key with quote",
      input = "\"tutu\"",
      rule = _.Key.run(),
      fvalidate = 
        _ match {
            case Success(deepAttrs) => 
              deepAttrs should have size 1
              deepAttrs should contain only ("tutu")
            case _ => fail()
          }
  )
  
  testRule[List[String]](
      title = "dotted key",
      input = "tutu.toto",
      rule = _.Key.run(),
      fvalidate = 
         _ match {
            case Success(deepAttrs) => 
              deepAttrs should have size 2
              deepAttrs should contain only ("tutu", "toto")
            case _ => fail()
          }
  )
  
  testRule[List[String]](
      title = "complex dotted key",
      input = "tutu.\"key 123\".toto",
      rule = _.Key.run(),
      fvalidate = 
        _ match {
            case Success(deepAttrs) => 
              deepAttrs should have size 3
              deepAttrs should contain only ("tutu", "key 123", "toto")
            case _ => fail()
          }
  )

  
}