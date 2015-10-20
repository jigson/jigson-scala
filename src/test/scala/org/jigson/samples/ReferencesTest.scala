package org.jigson.samples

import org.scalatest.Matchers
import org.jigson.model.JIGValue
import org.jigson.model.JIGObject
import scala.util.Success
import org.jigson.model.JIGString
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ReferencesTest extends TestParser with Matchers {
   testRule[JIGValue](
      title = "Test referenced value",
      input = """
toto : "string1"
tutu @toto

""",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        
        case Success(JIGObject(attrs)) => 
        case _ => fail()
      }
  )

  testRule[JIGValue](
      title = "Test deep referenced value",
      input = """
toto : "string1"
tutu : { attr1 = "attr1Value" }

tataEqTutu : @tutu.attr1

""",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        
        case Success(JIGObject(attrs)) => 
        case _ => fail()
      }
  )

  testRule[JIGValue](
      title = "Test nested deep referenced value",
      input = """
toto : "string1"
tutu : { attr1 = false 
        attr2 = "pouete"
}

tata : { attrTata : @tutu}

""",
      rule = _.Input.run(),
      fvalidate = 
        _ match {
        
        case Success(JIGObject(attrs)) => 
        case _ => fail()
      }
  )
  
  testRule[JIGValue](
      title = "dble references @@",
      input = """
a { a1 "b" }
b "hello world"
c @@a.a1 

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) => attrs.get("c") foreach(cVal =>  cVal.toString should equal ("hello world") ) 
        case _ => fail()
        
      }
  )
  
  testRule[JIGValue](
      title = "tripple reference",
      input = """
a { a1 "c" }
b "a1"
c "hello world"
d @@a.@b 

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) => attrs.get("d") foreach(dVal =>  dVal.toString should equal ("hello world") ) 
        case _ => fail()
        
      }
  )
  
  testRule[JIGValue](
      title = "merging object",
      input = """
a { a1 "c" a2 false }
b { @a,
  b1 = "tutu"
}

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) =>  
        case _ => fail()
        
      }
  )
  
  
    testRule[JIGValue](
      title = "merging into root obj",
      input = """
a { a1 "c" a2 false }

@a

b { b1 
    {
       b11 : null b22 "toto" }
}

c { @b.b1, id = 30 }
d { @b, id = 40 }

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) =>  
        case _ => fail()
        
      }
  )
  
      testRule[JIGValue](
      title = "merging object 2",
      input = """

b { b1 
    {
       b11 : null b22 "toto" }
}

c { @b.b1 }
d { @b }

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) =>  
        case _ => fail()
        
      }
  )

  testRule[JIGValue](
      title = "merging object without comma",
      input = """
a { a1 "c" a2 false }
b { @a
  b1 = "tutu"
}

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(obj) => val bb1 = obj.as[JIGObject]().get[JIGObject]("b").get[JIGString]("b1").value;
        println ("b.b1 = " + bb1);
        case _ => fail()
        
      }
  )

     testRule[JIGValue](
      title = "reference for a key",
      input = """
a { a1 "c" a2 false }
@a.a1 "tutu" 

""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(JIGObject(attrs)) => attrs.get("c") foreach(dVal =>  dVal.toString should equal ("tutu") )
        case _ => fail()
        
      }
  )

  testRule[JIGValue](
      title = "referenced value in an array",
      input = """
a = "toto"
b = { b1 true b2 null }
c = (1 2 3 4)  
d = ( "tutu" @a @b @c )
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(attrs) =>  
        case _ => fail()
        
      }
  )
  
  testRule[JIGValue](
      title = "merge array and object into an array",
      input = """
b = { b1 true b2 null }
c = (1 2 3 4)
d = ( "tutu" <@b <@c )
""",
      rule = _.Input.run(),
      fvalidate =
        _ match {
        case Success(attrs) =>  
        case _ => fail()
        
      }
  )

  
  
}