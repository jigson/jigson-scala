package com.jigson.parser.test

import org.scalatest.Matchers
import org.jigson.model.JIGValue
import scala.util.Success

class TestEntries extends TestParser with Matchers {
    testRule[(List[String], JIGValue)](
      title = "simple entry",
      input = "attr1 = \"value1\"",
      rule = _.Entry.run(),
      fvalidate =  
        _ match {
          case Success(obj) => //TODO test
          case _ => fail()
        } 
  )
  
  testRule[(List[String], JIGValue)](
      title = "simple entry 2",
      input = "\"attr2\" : true",
      rule = _.Entry.run(),
      fvalidate = 
        _ match {
            case Success(obj) => // TODO test
            case _ => fail()
          }
    
  )
  
  testRule[(List[String], JIGValue)](
      title = "complexe entry 2",
      input = "\"attr2\".attr3 : null",
      rule = _.Entry.run(),
      fvalidate = 
        _ match {
      case Success(obj) => // TODO test
      case _ => fail()
    }
  )

  testRule[Seq[(List[String], JIGValue)]](
      title = "multiple entries 1",
      input = "\"attr1\".attr11 : null",
      rule = _.Entries.run(),
      fvalidate = 
        _ match {
            case Success(obj) => 
              obj should have length 1
            case _ => fail()
          }
  )
  
  testRule[Seq[(List[String], JIGValue)]](
      title = "multiple entries 2",
      input = "\"attr1\".attr11 : null\nattr2 = false attr3.attr31  \"value3\"",
      rule = _.Entries.run(),
      fvalidate = 
        _ match {
            case Success(obj) => 
              obj should have length 3
            case _ => fail()
          }
  )

}