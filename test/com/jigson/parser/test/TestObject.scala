package com.jigson.parser.test

import org.scalatest.Matchers
import org.jigson.model.JIGObject
import scala.util.Success

class TestObject extends TestParser with Matchers {
    testRule[JIGObject](
      title = "simple object creation",
      input = """{ attr1 "value1"
"attr2" : true
}""",
      rule = _.Object.run(),
      fvalidate = 
        _ match {
            case Success(obj) => // TODO test
            case _ => fail()
          }
  )
  
  testRule[JIGObject](
      title = "simple object creation with comma",
      input = """{ attr1 "value1",
"attr2" : true
}""",
      rule = _.Object.run(),
      fvalidate = 
      _ match {
          case Success(obj) => 
          case _ => fail()
        }
  )
  
  
  testRule[JIGObject](
      title = "simple object creation 2",
      input = """{ attr1 "value1"
"attr2".attr22 : true
}""",
      rule = _.Object.run(),
      fvalidate = 
        _ match {
          case Success(obj) => 
          case _ => fail()
        } 
  )
  
  
  testRule[JIGObject](
      title = "simple nested simple object creation 1",
      input = """{ attr1 "value1"
"attr2" : 
  {
    attr21 : true
    attr22 "valeur string de 22"
  }
}""",
    
      rule = _.Object.run(),
      fvalidate = 
        _ match {
            case Success(obj) => 
            case _ => fail()
          }
  )
  
  testRule[JIGObject](
      title = "simple obj nested with complexe attr creation 2",
      input = """{ attr1 "value1"
"attr2" : 
  {
    attr21 : true
  }
attr2.attr22 "valeur string de 22"
}""",
      rule = _.Object.run(),
      fvalidate = 
        _ match {
            case Success(obj) => 
            case _ => fail()
          } 
  )
  
  testRule[JIGObject](
      title = "complexe nested simple object creation 3 with comment",
      input = """{ attr1 "value1"
"attr2" : 
  {
    attr21 : true  /*Commentaire */
  } %{ Commentaire 2
sur plusieurs ligne }%
attr2.attr22 "valeur string de 22"
}""",
      rule = _.Object.run(),
      fvalidate = 
        _  match {
            case Success(obj) => 
            case _ => fail()
          }
  )

}